package com.sptech.qujj.model;

import android.webkit.JavascriptInterface;

import com.sptech.qujj.activity.WebViewActivity;

public class JSObject {
	private WebViewActivity context;

	public JSObject(WebViewActivity context) {
		this.context = context;
	}

	@JavascriptInterface
	public String invite() {
		this.context.invite();
		return "";
	}

	@JavascriptInterface
	public String share() {
		this.context.share();
		return "";
	}
}
