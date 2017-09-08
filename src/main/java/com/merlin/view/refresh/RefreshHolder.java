package com.merlin.view.refresh;

public class RefreshHolder {

	public int mOffsetY;

	public void move(int deltaY) {
		mOffsetY += deltaY;
	}

	public boolean hasHeaderPullDown() {
		return mOffsetY > 0;
	}

	public boolean hasFooterPullUp() {
		return mOffsetY < 0;
	}
	public boolean isOverHeader(int deltaY){
		return mOffsetY<-deltaY;
		
	}
}
