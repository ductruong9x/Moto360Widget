package com.appfree.moto360widget;


import android.app.Application;

import com.appfree.moto360widget.network.MyVolley;

public class MyApplication extends Application {

	public static String name;
	public static String avater;
	public static String time, content, post_id;
	public static int like = 0;
	public static boolean isNotification, isLoadImage;

	public static boolean isNotification() {
		return isNotification;
	}

	public static void setNotification(boolean isNotification) {
		MyApplication.isNotification = isNotification;
	}

	public static boolean isLoadImage() {
		return isLoadImage;
	}

	public static void setLoadImage(boolean isLoadImage) {
		MyApplication.isLoadImage = isLoadImage;
	}

	public static int getLike() {
		return like;
	}

	public static void setLike(int like) {
		MyApplication.like = like;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MyVolley.init(this);
	}

	public static String getTime() {
		return time;
	}

	public static void setTime(String time) {
		MyApplication.time = time;
	}

	public static String getContent() {
		return content;
	}

	public static void setContent(String content) {
		MyApplication.content = content;
	}

	public static String getPost_id() {
		return post_id;
	}

	public static void setPost_id(String post_id) {
		MyApplication.post_id = post_id;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		MyApplication.name = name;
	}

	public static String getAvater() {
		return avater;
	}

	public static void setAvater(String avater) {
		MyApplication.avater = avater;
	}

}
