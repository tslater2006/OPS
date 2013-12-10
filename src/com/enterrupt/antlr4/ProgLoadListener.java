package com.enterrupt.antlr4;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.interpreter.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.antlr4.frontend.*;

public class ProgLoadListener extends PeopleCodeBaseListener {

	private int recurseLvl;
	private PeopleCodeProg srcProg;
	private ProgLoadSupervisor supervisor;
	private BufferedTokenStream tokens;
	private Map<Integer, Void> refIndicesSeen;
    private ParseTreeProperty<PeopleCodeProg> varTypeProgs = new ParseTreeProperty<PeopleCodeProg>();

    private void setVarTypeProg(ParseTree node, PeopleCodeProg prog) {
        this.varTypeProgs.put(node, prog);
    }

    private PeopleCodeProg getVarTypeProg(ParseTree node) {
        if(this.varTypeProgs.get(node) == null) {
            throw new EntVMachRuntimeException("No program was found for the requested variable " +
                "type node: " + node.getText());
        }
        return this.varTypeProgs.get(node);
    }

	private static Logger log = LogManager.getLogger(ProgLoadListener.class.getName());

	public ProgLoadListener(PeopleCodeProg srcProg, int recurseLvl,
			ProgLoadSupervisor supervisor,  BufferedTokenStream tokens) {
		this.srcProg = srcProg;
		this.tokens = tokens;
		this.recurseLvl = recurseLvl;
		this.supervisor = supervisor;
		this.refIndicesSeen = new HashMap<Integer, Void>();
	}

	/**
	 * When an app package/class is imported, load the root package's defn
	 * and save the package path for use during potential class resolution later.
	 */
	@Override
	public void exitAppClassImport(PeopleCodeParser.AppClassImportContext ctx) {
		if(ctx.appPkgPath() != null) {
			String rootPkgName = ctx.appPkgPath().GENERIC_ID(0).getText();
			DefnCache.getAppPackage(rootPkgName);
			this.srcProg.importedAppPackagePaths.add(new AppPackagePath(
				ctx.appPkgPath().getText()));
		} else {
			String rootPkgName = ctx.appClassPath().GENERIC_ID(0).getText();
			DefnCache.getAppPackage(rootPkgName);
			String appClassName = ctx.appClassPath().GENERIC_ID(
				ctx.appClassPath().GENERIC_ID().size() - 1).getText();
			List<AppPackagePath> pathsToClass = this.srcProg.importedAppClasses.get(appClassName);
			if(pathsToClass == null) {
				pathsToClass = new ArrayList<AppPackagePath>();
			}
			pathsToClass.add(new AppPackagePath(ctx.appClassPath().getText()));
			this.srcProg.importedAppClasses.put(appClassName, pathsToClass);
		}
	}

	/**
	 * If a property statment in an App Class program references another app class object,
	 * immediately load the program containing that app class.
	 */
	@Override
	public void exitInstance(PeopleCodeParser.InstanceContext ctx) {
		if(ctx.varType().appClassPath() != null || ctx.varType().GENERIC_ID() != null) {
			this.handlePropOrInstanceAppClassRef(this.getVarTypeProg(ctx.varType()));
		}
	}

	/**
	 * If an instance statment in an App Class program references another app class object,
	 * immediately load the program containing that app class.
	 */
	@Override
	public void exitProperty(PeopleCodeParser.PropertyContext ctx) {
		if(ctx.varType().appClassPath() != null || ctx.varType().GENERIC_ID() != null) {
			this.handlePropOrInstanceAppClassRef(this.getVarTypeProg(ctx.varType()));
		}
	}

	/**
	 * When a variable type in a variable declaration in a non-app class program references
	 * an object in an app class, we need to parse out the app class reference and
	 * load that program's OnExecute PeopleCode segment.
	 */
	@Override
	public void exitVarType(PeopleCodeParser.VarTypeContext ctx) {

		List<String> appClassParts = null;

		if(ctx.appClassPath() != null) {
			appClassParts = new ArrayList<String>();
			for(TerminalNode id : ctx.appClassPath().GENERIC_ID()) {
				appClassParts.add(id.getText());
			}
			//log.debug("(0) Path found: {} in {}", appClassParts, ctx.getText());
		} else if(ctx.GENERIC_ID() != null) {
			appClassParts = this.resolveAppClassToFullPath(ctx.GENERIC_ID().getText());
			//log.debug("(1) Class name resolved: {} in {}", appClassParts, ctx.getText());
		} else {
			return;
		}

		PeopleCodeProg prog = new AppClassPeopleCodeProg(appClassParts.toArray(
			new String[appClassParts.size()]));

		/**
		 * App class programs should not have all their variable types loaded, just
		 * those that appear in their instance/property statements. All other programs
	 	 * should load object var types at any point in the program.
		 */
		if(!(this.srcProg instanceof AppClassPeopleCodeProg)) {
			prog = DefnCache.getProgram(prog);
			this.srcProg.referencedProgs.add(prog);

			// Load the referenced program's initial metadata.
			prog.init();
		} else {
			// The exitInstance/exitProperty methods need a reference to the resolved prog.
			this.setVarTypeProg(ctx, prog);
		}
	}

	/**
	 * Detect references to externally-defined functions. We make note of the
	 * referenced program at this time; later, once we've counted any and all
	 * function calls to this and other functions defined in the referenced program,
	 * that program will itself be loaded, provided that the number of references is > 0.
	 */
	@Override
	public void exitExtFuncImport(PeopleCodeParser.ExtFuncImportContext ctx) {

		if(this.srcProg instanceof AppClassPeopleCodeProg) {
			return;
		}

		String fnName = ctx.GENERIC_ID().getText();
		String recname = ctx.recDefnPath().GENERIC_ID(0).getText();
		String fldname = ctx.recDefnPath().GENERIC_ID(1).getText();

		// Load the record defn it it hasn't already been cached.
		DefnCache.getRecord(recname);

		PeopleCodeProg prog = new RecordPeopleCodeProg(recname, fldname,
			ctx.event().getText());
		prog = DefnCache.getProgram(prog);

		this.srcProg.referencedProgs.add(prog);
		this.srcProg.recordProgFnCalls.put(fnName, (RecordPeopleCodeProg)prog);

		// Load the prog's initial metadata if it hasn't already been cached.
		prog.init();
	}

	/**
	 * Detect create stmts, which reference app classes. Upon encountering one,
	 * we need to load the app class OnExecute program corresponding to the class
	 * instance being created.
	 */
	@Override
	public void exitCreateInvocation(PeopleCodeParser.CreateInvocationContext ctx) {

		if(this.srcProg instanceof AppClassPeopleCodeProg) {
			return;
		}
		List<String> appClassParts = null;

		if(ctx.appClassPath() != null) {
			/**
			 * This invocation of 'create' includes the full path to the app class object
			 * being instantiated.
			 */
			appClassParts = new ArrayList<String>();
			for(TerminalNode id : ctx.appClassPath().GENERIC_ID()) {
				appClassParts.add(id.getText());
			}
		} else {
			/**
			 * This invocation of 'create' is creating an instance of an app class,
			 * but we only have the app class name.
			 */
			appClassParts = this.resolveAppClassToFullPath(ctx.GENERIC_ID().getText());
		}

		PeopleCodeProg prog = new AppClassPeopleCodeProg(appClassParts.toArray(
			new String[appClassParts.size()]));
		prog = DefnCache.getProgram(prog);
		this.srcProg.referencedProgs.add(prog);

		// Load the referenced program's initial metadata.
		prog.init();
	}

	/**
	 * Detect function (*not* method) calls; if a call corresponds to a
	 * function referenced in a previously seen "Declare" stmt, mark that program
	 * as having at least one call to it. Calls to index into a rowset using
	 * PeopleCode's "(<int>)" syntax should be ignored here.
	 */
	@Override
	public void exitExprFnOrRowsetCall(PeopleCodeParser.ExprFnOrRowsetCallContext ctx) {

		PeopleCodeParser.IdContext id = null;

		if(ctx.expr() instanceof PeopleCodeParser.ExprIdContext) {
			id = ((PeopleCodeParser.ExprIdContext)ctx.expr()).id();
		} else if(ctx.expr() instanceof PeopleCodeParser.ExprMethodOrStaticRefContext) {
			/**
		     * If a method call is the expr on which this fn/rowset call operates on,
			 * i.e., "&arr.Push(%Menu)", we can ignore it; methods cannot be the subject
			 * of "Declare" stmts.
			 */
			return;
		} else if(ctx.expr() instanceof PeopleCodeParser.ExprFnOrRowsetCallContext) {
			/**
			 * If a function call is the expr on which this fn/rowset call operates on,
			 * i.e., "GetRowset(Scroll.ENRL_REQ_DETAIL)(1)", we can ignore it;
			 * the name of the function was already processed by the explicit call to
			 * visit(ctx.expr()) above.
			 */
			return;
		} else {
			throw new EntVMachRuntimeException("Encountered unexpected expression type "+
				"preceding a function call or rowset index call: " + ctx.expr().getText());
		}

		/**
		 * Only GENERIC_IDs are considered to be identifiers representative of
		 * a function name; if the identifier is a variable, it is
		 * part of a rowset call (i.e, "&rs(1)") and should not be checked.
		 */
		if(id.GENERIC_ID() != null) {

			RecordPeopleCodeProg referencedProg = this.srcProg.recordProgFnCalls
				.get(id.GENERIC_ID().getText());

			/**
			 * If the referencedProg is null, the referenced function has a scope
			 * beyond the program (is a system function). Otherwise, the function
			 * is in program scope, and is thus defined in the referencedProg;
			 * we must note that a reference to that program exists to ensure that
			 * we load that program later.
			 */
			if(referencedProg != null) {
				srcProg.confirmedRecordProgCalls.put(referencedProg, true);
			}
		}
	}

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		int tokPos = ctx.getStart().getTokenIndex();
		List<Token> refChannel = tokens.getHiddenTokensToLeft(tokPos,
			PeopleCodeLexer.REFERENCES);
		Token refTok;

		if(refChannel != null && (refTok = refChannel.get(0)) != null) {
			String text = refTok.getText();
			int refIdx = Integer.parseInt(text.substring(8, text.length() - 1));

			/**
			 * If we've already seen this reference, no need to process it again.
			 */
			if(!this.refIndicesSeen.containsKey(refIdx)) {
				Reference refObj = this.srcProg.progRefsTable.get(refIdx);

				if(refObj.isRecordFieldRef
					&& ((srcProg instanceof RecordPeopleCodeProg && recurseLvl < 4)
							|| (srcProg instanceof ComponentPeopleCodeProg && recurseLvl < 2))) {
					DefnCache.getRecord(refObj.RECNAME);
				}
				this.refIndicesSeen.put(refIdx, null);
			}
		}
	}

	/************************************************************************/
	/** shared functions ****************************************************/
	/************************************************************************/

	/**
	 * We first search for the class name in the table of app class imports.
	 * If no entry exists there, we scan the package imports for a match.
	 */
	private List<String> resolveAppClassToFullPath(String appClassName) {
		AppPackagePath authoritativePath = null;
		List<AppPackagePath> pkgList = this.srcProg.importedAppClasses.get(appClassName);
		List<String> appClassParts = null;

		if(pkgList != null) {
			if(pkgList.size() > 1) {
				throw new EntVMachRuntimeException("Found multiple discrete app class imports " +
					"for an app class; unable to resolve authoritative package path.");
			} else {
				authoritativePath = pkgList.get(0);
			}
		} else {
			for(AppPackagePath importedPkgPath : this.srcProg.importedAppPackagePaths) {
				AppPackage pkg = DefnCache.getAppPackage(importedPkgPath.getRootPkgName());
				Map<String, Void> appClassesInPath = pkg.getClassesInPath(importedPkgPath);
				if(appClassesInPath.containsKey(appClassName)) {
					if(authoritativePath == null) {
						authoritativePath = importedPkgPath;
					} else {
						throw new EntVMachRuntimeException("Found multiple discrete app pkg " +
							"imports for an app class; unable to resolve authoritative package path.");
					}
				}
			}
		}

		if(authoritativePath != null) {
			appClassParts = new ArrayList<String>();
			for(String part : authoritativePath.parts) {
				appClassParts.add(part);
			}
			appClassParts.add(appClassName);
		} else {
			throw new EntVMachRuntimeException("Unable to resolve authoritative path to class name (" + 
				appClassName + ").");
		}

		return appClassParts;
	}

	/**
	 * This method should be called to immediately load programs referenced in an App Class
	 * program within a property or instance statement.
	 */
	private void handlePropOrInstanceAppClassRef(PeopleCodeProg prog) {
		prog = DefnCache.getProgram(prog);
		this.srcProg.referencedProgs.add(prog);

		// Load the referenced program's initial metadata.
		prog.init();

		// Load the program's referenced defns and programs immediately.
		supervisor.loadImmediately(prog);
	}
}
