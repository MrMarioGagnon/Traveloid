package com.gagnon.mario.mr.traveloid.core.dialog;

public interface MultipleChoiceEventHandler {
	void execute(boolean[] idSelected);

	MultipleChoiceEventHandler NO_OP = new MultipleChoiceEventHandler() {
		public void execute(boolean[] idSelected) {
		}
	};
}
