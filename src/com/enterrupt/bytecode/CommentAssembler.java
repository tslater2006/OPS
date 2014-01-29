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

import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class CommentAssembler extends ElementAssembler {

	private byte b;

	public CommentAssembler(byte _b) {
		this(_b, AFlag.NEWLINE_BEFORE_AND_AFTER);
	}

	public CommentAssembler(byte _b, int _f) {
		this.b = _b;
		this.format = _f;
	}

	public byte getStartByte() {
		return b;
	}

	public void assemble(PeopleCodeByteStream stream) {

		// Length byte is wide ANDed and cast to integer.
		int commLen = (int) stream.readNextByte() & 0xff;
		commLen = commLen + ((int) stream.readNextByte() & 0xff) * 256;

		byte b;
		for(int i=0; i < commLen
				&& (stream.getCursorPos() < stream.getProgLenInBytes()); i++) {
			b = stream.readNextByte();
			if(b != 0) {
				if(b == (byte) 10) {
					stream.appendAssembledText('\n');
				} else {
					stream.appendAssembledText((char) b);
				}
			}
		}
	}

	public boolean writesNonBlank() {
		return true;
	}
}
