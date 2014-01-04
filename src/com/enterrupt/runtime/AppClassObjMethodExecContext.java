package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.tree.*;

public class AppClassObjMethodExecContext extends AppClassObjExecContext {

    public AppClassObjMethodExecContext(PTAppClassObj obj, String m,
				ParseTree s, PTType r) {
        super(obj, m, s, r);
    }
}

