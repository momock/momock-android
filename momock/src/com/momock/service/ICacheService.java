package com.momock.service;

import java.io.File;

public interface ICacheService extends IService{
	File getCacheDir();
	File getCacheOf(String uri);
}
