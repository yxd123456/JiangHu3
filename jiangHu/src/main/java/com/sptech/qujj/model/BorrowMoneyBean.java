package com.sptech.qujj.model;

import java.io.Serializable;

/**
 * 借款详情实体类
 * 
 * @author LiLin
 * 
 */
public class BorrowMoneyBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String loan_order_id;// 交易账号
	private String title;// 交易类型
	private float help_money;// 交易金额
	private String card_bank;// 接收卡开户行
	private String card_no;// 接收卡卡号
	private String frozen_card_no;// 申请卡卡号
	private String frozen_card_bank;// 申请卡开户行
	private int addtime;// 申请时间
	private int takeeffect_time;// 放款时间

	public String getLoan_order_id() {
		return loan_order_id;
	}

	public void setLoan_order_id(String loan_order_id) {
		this.loan_order_id = loan_order_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getHelp_money() {
		return help_money;
	}

	public void setHelp_money(float help_money) {
		this.help_money = help_money;
	}

	public String getCard_bank() {
		return card_bank;
	}

	public void setCard_bank(String card_bank) {
		this.card_bank = card_bank;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getFrozen_card_no() {
		return frozen_card_no;
	}

	public void setFrozen_card_no(String frozen_card_no) {
		this.frozen_card_no = frozen_card_no;
	}

	public String getFrozen_card_bank() {
		return frozen_card_bank;
	}

	public void setFrozen_card_bank(String frozen_card_bank) {
		this.frozen_card_bank = frozen_card_bank;
	}

	public int getAddtime() {
		return addtime;
	}

	public void setAddtime(int addtime) {
		this.addtime = addtime;
	}

	public int getTakeeffect_time() {
		return takeeffect_time;
	}

	public void setTakeeffect_time(int takeeffect_time) {
		this.takeeffect_time = takeeffect_time;
	}

}
