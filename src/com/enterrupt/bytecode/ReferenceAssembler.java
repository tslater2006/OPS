package com.enterrupt.bytecode;

/**
 * ============================================================
 * This file contains source code derived from the excellent
 * Decode PeopleCode open source project, maintained by Erik H
 * and available under the ISC license at
 * http://sourceforge.net/projects/decodepcode/. The associated
 * license text has been reproduced here in accordance with
 * the license requirements.
 * ============================================================
 * Copyright (c)2011 Erik H (erikh3@users.sourceforge.net)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

import com.enterrupt.pt.Reference;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class ReferenceAssembler extends ElementAssembler {

	byte b;

	public ReferenceAssembler(byte _b) {
		this.b = _b;
		this.format = AFlag.SPACE_BEFORE_AND_AFTER;
	}

	public byte getStartByte() {
		return b;
	}

	public void assemble(PeopleCodeByteStream stream) {

		int b1 = (int) (stream.readNextByte() & 0xff);
		int b2 = (int) (stream.readNextByte() & 0xff);

		int refIdx = b2 * 256 + b1 + 1;
		Reference refObj = stream.getMappedReference(refIdx);
		if(refObj == null) {
			throw new EntAssembleException("No reference is mapped to index " + refIdx + " on the "
				+ "program underlying this stream.");
		}

		String ref = refObj.getValue();

		/**
		 * If the reference refers to a data buffer or component buffer
		 * chained to an expression before it using the dot (".") operator,
		 * we need to strip the defn type (Field,Record,Scroll) from the reference.
		 */
		if(b == 74 && (ref.startsWith("Field.") ||
				ref.startsWith("Record.") || ref.startsWith("Scroll."))) {
			ref = ref.substring(ref.indexOf('.') + 1);
		}

		/**
		 * TODO: Reach a better understanding of the purpose of this.
		 * If the exception below is ever encountered, setup a hidden parser channel
		 * and emit the before and after reference string for analysis.
		 */
		int p1 = ref.indexOf('.');
		if(b == (byte) 72 && p1 > 0) {
			String rec = ref.substring(0, p1);
			ref = rec + ".\"" + ref.substring(p1 + 1) + "\"";
			throw new EntAssembleException("Encountered byte 72 during assembly of reference text; "
				+ "investigate this.");
		}

		/**
		 * Before emitting the reference text, emit the reference index.
		 * This is required during component loading.
		 */
		stream.appendAssembledText("#ENTREF{"+refIdx+"}");
		stream.appendAssembledText(ref);
	}
}
