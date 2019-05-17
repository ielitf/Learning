/**
 * 优分享VR
 * copy right: youkes.com
 * author:xuming
 * licence:GPL2
 */
package com.litf.learning.bean;

/**
 * ============================================================
 * Copyright：${TODO}有限公司版权所有 (c) 2017
 * Author：   AllenIverson
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChen1999
 * 博客：     http://blog.csdn.net/axi295309066
 * 微博：     AndroidDeveloper
 * GitBook：  https://www.gitbook.com/@alleniverson
 * <p>
 * Project_Name：GoogleVR
 * Package_Name：com.google.vr
 * Version：1.0
 * time：2016/3/10 0:49
 * des ：${TODO}
 * gitVersion：2.12.0.windows.1
 * updateAuthor：$Author$
 * updateDate：$Date$
 * updateDes：${TODO}
 * ============================================================
 */

public class VideoItem {
	public String title;
	public String type;//视屏类型
	public String play;//视频播放地址
	public String img;//图片地址
	public String dateCnSimple;//日期

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlay() {
		return play;
	}

	public void setPlay(String play) {
		this.play = play;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getDateCnSimple() {
		return dateCnSimple;
	}

	public void setDateCnSimple(String dateCnSimple) {
		this.dateCnSimple = dateCnSimple;
	}

	@Override
	public String toString() {
		return "VideoItem{" +
				"title='" + title + '\'' +
				", type='" + type + '\'' +
				", play='" + play + '\'' +
				", img='" + img + '\'' +
				", dateCnSimple='" + dateCnSimple + '\'' +
				'}';
	}
}
