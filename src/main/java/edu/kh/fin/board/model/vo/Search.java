package edu.kh.fin.board.model.vo;

import java.util.Arrays;

public class Search {

	private String sk;
	private String sv;
	private String boardType;
	private int[] ct;
	
	
	public Search() {}


	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public String getSv() {
		return sv;
	}

	public void setSv(String sv) {
		this.sv = sv;
	}

	public String getBoardType() {
		return boardType;
	}

	public void setBoardType(String boardType) {
		this.boardType = boardType;
	}

	public int[] getCt() {
		return ct;
	}

	public void setCt(int[] ct) {
		this.ct = ct;
	}

	@Override
	public String toString() {
		return "Search [sk=" + sk + ", sv=" + sv + ", boardType=" + boardType + ", ct=" + Arrays.toString(ct) + "]";
	}
	
}
