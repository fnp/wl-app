package com.moiseum.wolnelektury.connection.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Piotr Ostrowski on 23.06.2018.
 */
public class ReadingStateModel {

	private static final String UNKNOWN = "unknown";
	private static final String NOT_STARTED = "not_started";
	private static final String READING = "reading";
	private static final String COMPLETED = "complete";

	public enum ReadingState {
		@SerializedName(UNKNOWN)
		STATE_UNKNOWN {
			@Override
			public String getStateName() {
				return UNKNOWN;
			}
		},
		@SerializedName(NOT_STARTED)
		STATE_NOT_STARTED {
			@Override
			public String getStateName() {
				return NOT_STARTED;
			}
		},
		@SerializedName(READING)
		STATE_READING {
			@Override
			public String getStateName() {
				return READING;
			}
		},
		@SerializedName(COMPLETED)
		STATE_COMPLETED {
			@Override
			public String getStateName() {
				return COMPLETED;
			}
		};

		public abstract String getStateName();
	}

	private ReadingState state;

	public ReadingStateModel() {
		this.state = ReadingState.STATE_UNKNOWN;
	}

	public ReadingState getState() {
		return state;
	}

	public void setState(ReadingState state) {
		this.state = state;
	}
}
