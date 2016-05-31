package com.gagnon.mario.mr.traveloid.category;

import com.gagnon.mario.mr.traveloid.core.ObjectBase;

import java.io.Serializable;
import java.util.Arrays;

//import com.google.common.base.Preconditions;
//import com.mg.grandgalot.traveloid.core.ObjectBase;

public class Category extends ObjectBase implements Serializable {

	private static final long serialVersionUID = 630637256262739065L;

	public static Category create(Long id, String name, String[] subCategories) {

		//Preconditions.checkArgument(!(null == id), "Argument id is mandatory.");
	
		Category category = new Category();
		category.mNew = false;
		category.mDirty = false;
		category.mId = id;
		category.mName = name;
		category.mSubCategories = subCategories;

		return category;
	}

	public static Category createNew() {

		Category category = new Category();
		category.mNew = true;
		category.mDirty = true;

		return category;
	}

	private Long mId;

	private String mName;

	private String[] mSubCategories;

	private Category() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
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
        return Arrays.equals(mSubCategories, other.mSubCategories);
    }

	public Long getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String[] getSubCategories() {
		return mSubCategories;
	}

	public String getSubCategoriesAsString() {

		StringBuilder sb = new StringBuilder();

		for (String subCategory : getSubCategories()) {

			if (sb.length() != 0) {
				sb.append("|");
			}
			sb.append(subCategory);

		}

		return sb.toString();
	}

	public String getSubCategory(int i) {

		if (i > getSubCategories().length - 1)
			return null;

		return getSubCategories()[i];

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		result = prime * result + Arrays.hashCode(mSubCategories);
		return result;
	}

	public void setId(Long id) {
		mId = id;
	}

	public void setName(String name) {

		if (null == mName || !mName.equals(name)) {
			mName = name;
			mDirty = true;
		}

	}

	public void setSubCategories(String[] subCategories) {

		if (null == mSubCategories
				|| !Arrays.equals(mSubCategories, subCategories)) {
			mSubCategories = subCategories;
			mDirty = true;
		}

	}

    @Override
	public String toString() {
		return getName();
	}

}
