package com.gagnon.mario.mr.traveloid.traveler;

import java.io.Serializable;

import com.gagnon.mario.mr.traveloid.core.ObjectBase;

public class Traveler extends ObjectBase implements Serializable, Comparable<Traveler> {

	private static final long serialVersionUID = -8317508437281790337L;

	public static Traveler create(Long id, String name) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");

		Traveler traveler = new Traveler();
		traveler.mNew = false;
		traveler.mDirty = false;
		traveler.mId = id;
		traveler.mName = name;

		return traveler;
	}

	public static Traveler createNew() {

		Traveler traveler = new Traveler();
		traveler.mNew = true;
		traveler.mDirty = true;
		traveler.mName = "";

		return traveler;

	}

	private Long mId = null;

	private String mName;

	private Traveler() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Traveler other = (Traveler) obj;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
			return false;
		return true;
	}

	public Long getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		return result;
	}

	public void setId(Long id) {
		mId = id;
	}

	public void setName(String name) {

		if (!mName.equals(name)) {
			mDirty = true;
			mName = name;
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Traveler another) {
		return getName().compareToIgnoreCase(another.getName());
	}

}
