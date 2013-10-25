package com.enterrupt.buffers;

public interface IStreamableBuffer {

	public abstract IStreamableBuffer next();
	public abstract void resetCursors();
}
