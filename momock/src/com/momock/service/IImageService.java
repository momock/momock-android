package com.momock.service;

import com.momock.holder.ImageHolder;

import android.graphics.Bitmap;

public interface IImageService extends IService{

	public static interface ImageSetter{
		void setImage(Bitmap bitmap);
	}
	
	void load(ImageHolder holder, ImageSetter setter);
}
