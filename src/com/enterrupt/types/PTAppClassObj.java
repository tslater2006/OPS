package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class PTAppClassObj extends PTObjectType {

	private static Type staticTypeFlag = Type.APP_CLASS_OBJ;
	public AppClassPeopleCodeProg progDefn;
	public Scope instanceScope;

	protected PTAppClassObj(AppClassPeopleCodeProg prog) {
		super(staticTypeFlag);
		this.progDefn = prog;
		this.instanceScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_INSTANCE);

		// Load instance identifiers into instance scope.
		for(Map.Entry<String, AppClassPeopleCodeProg.Instance> cursor :
				this.progDefn.instanceTable.entrySet()) {
			AppClassPeopleCodeProg.Instance instance = cursor.getValue();

			/**
			 * Primitive instance variables must have space allocated for them
			 * immediately. Object instance variables should simply reference the
			 * sentinel type value present in their declaration statement.
			 */
			if(instance.type instanceof PTPrimitiveType) {
				this.instanceScope.declareVar(instance.id,
					((PTPrimitiveType)instance.type).alloc());
			} else {
				this.instanceScope.declareVar(instance.id, instance.type);
			}
		}
	}

   /**
	* Private properties
	* are private to the *CLASS*, not the instances of the class themselves.
    */
	public PTType dotProperty(String s) {

		// Remember: instance variables are prefaced with ampersands in class declaration.
		if(this.progDefn.instanceTable.containsKey("&"+s)) {
			if(this.progDefn.instanceTable.get("&"+s).aLevel == AccessLevel.PUBLIC) {
				return this.instanceScope.resolveVar("&"+s);
			} else {
				throw new EntVMachRuntimeException("Encountered request for non-public "+
					"instance var; need to determine if the calling entity is an obj "+
					"with the same app class type as this one, since private properties "+
					"are private to all instances of the app class, not individual objects.");
			}
		}
		return null;
	}

   /**
	* Private methods
	* are private to the *CLASS*, not the instances of the class themselves.
    */
	public Callable dotMethod(String s) {
		if(this.progDefn.methodImplStartNodes.containsKey(s)) {
			if(this.progDefn.methodTable.get(s).aLevel == AccessLevel.PUBLIC) {
				return new Callable(new AppClassObjExecContext(this, s));
			} else {
				throw new EntVMachRuntimeException("Encountered request for non-public "+
					"method; need to determine if the calling entity is an obj "+
					"with the same app class type as this one, since private methods "+
					"are private to all instances of the app class, not individual objects.");
			}
		}
		return null;
	}

	public PTPrimitiveType castTo(PTPrimitiveType t) {
		throw new EntDataTypeException("castTo() has not been implemented.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTAppClassObj &&
			this.getType() == a.getType() &&
			this.progDefn == ((PTAppClassObj)a).progDefn);
	}

	public static PTAppClassObj getSentinel(AppClassPeopleCodeProg prog) {

		// If the sentinel has already been cached, return it immediately.
		String cacheKey = getCacheKey(prog);
		if(PTType.isSentinelCached(cacheKey)) {
			return (PTAppClassObj)PTType.getCachedSentinel(cacheKey);
		}

		// Otherwise, create a new sentinel type and cache it before returning it.
		PTAppClassObj sentinelObj = new PTAppClassObj(prog);
		PTType.cacheSentinel(sentinelObj, cacheKey);
		return sentinelObj;
	}

	public PTAppClassObj alloc() {
		PTAppClassObj newObj = new PTAppClassObj(this.progDefn);
		PTType.clone(this, newObj);
		return newObj;
	}

	private static String getCacheKey(AppClassPeopleCodeProg prog) {
		StringBuilder b = new StringBuilder(staticTypeFlag.name());
		b.append(":").append(prog.getDescriptor());
		return b.toString();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",prog=").append(this.progDefn.getDescriptor());
		return b.toString();
	}
}

