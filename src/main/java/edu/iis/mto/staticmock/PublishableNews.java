package edu.iis.mto.staticmock;

import java.util.ArrayList;
import java.util.List;

public class PublishableNews {

	public static PublishableNews create() {
		return new PublishableNews();
	}

	private final List<String> publicContent = new ArrayList<>();
	private final List<String> subscribentContent = new ArrayList<>();

	public void addPublicInfo(String content) {
		this.publicContent .add(content);
		
	}

	public void addForSubscription(String content, SubsciptionType subscriptionType) {
		// TODO Auto-generated method stub
	}

}
