package com.moiseum.wolnelektury.base;

import android.content.Context;
import android.content.Intent;

/**
 * Base abstract intent for activities.
 */

public abstract class AbstractIntent extends Intent {

	public AbstractIntent(final Context packageContext, final Class<?> cls) {
		super(packageContext, cls);
	}
}

