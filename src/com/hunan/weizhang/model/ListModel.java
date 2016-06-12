package com.hunan.weizhang.model;

public class ListModel implements Comparable<Object> {
	private int nameId;
	private String textName;
	private int imageId;

	public String getTextName() {
		return textName;
	}

	public void setTextName(String textName) {
		this.textName = textName;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public int getNameId() {
		return nameId;
	}

	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

    @Override
    public int compareTo(Object another) {
        if (!(another instanceof ListModel)) {
            return -1;
        }
        return this.textName.compareTo(((ListModel)another).textName);
    }
}
