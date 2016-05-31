package com.gagnon.mario.mr.traveloid.core.dialog;

public interface SingleChoiceEventHandler {
	void execute(int idSelected);

	SingleChoiceEventHandler NO_OP = new SingleChoiceEventHandler() {
		public void execute(int idSelected) {
		}
	};
}
