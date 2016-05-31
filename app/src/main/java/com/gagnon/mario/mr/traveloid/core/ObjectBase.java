package com.gagnon.mario.mr.traveloid.core;

import java.io.Serializable;

public abstract class ObjectBase  implements Serializable {
	
	private static final long serialVersionUID = 123604613062482253L;
	
	protected boolean mDirty;
	protected boolean mDead = false;
	protected boolean mNew;

	public boolean isDead() {
		return mDead;
	}

	public boolean isDirty() {
		return mDirty;
	}

	public boolean isNew() {
		return mNew;
	}
	
	public void setDead(boolean dead) {
		mDead = dead;
		mDirty = dead;
	}
	
	public void setNew(boolean new_){
		mNew = new_;
		mDirty = mNew;
	}
	
	
}
