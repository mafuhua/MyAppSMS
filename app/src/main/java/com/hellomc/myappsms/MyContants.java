package com.hellomc.myappsms;

import android.net.Uri;

/**
 * �ҵĳ�����
 * @author Administrator
 *
 */
public class MyContants {

	/**
	 * ֱ�Ӳ����������ݿ� sms ��� uri 
	 */
	public static final Uri URI_SMS = Uri.parse("content://sms");
	
	/**
	 * �ռ���
	 */
	public static final Uri URI_INBOX = Uri.parse("content://sms/inbox");
	/**
	 * ������
	 */
	public static final Uri URI_OUTBOX = Uri.parse("content://sms/outbox");
	/**
	 * �ݸ���
	 */
	public static final Uri URI_DRAFT = Uri.parse("content://sms/draft");
	/**
	 * �ѷ���
	 */
	public static final Uri URI_SENT = Uri.parse("content://sms/sent");
	
	
}
