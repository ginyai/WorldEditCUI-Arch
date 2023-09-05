package org.enginehub.worldeditcui.debug;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.enginehub.worldeditcui.config.CUIConfiguration;

final class DebugModeEnabledFilter extends AbstractFilter {
	private final CUIConfiguration config;

	DebugModeEnabledFilter(final CUIConfiguration config) {
		this.config = config;
	}

	private Result debugMode() {
		return config.isDebugMode() ? Result.NEUTRAL : Result.DENY;
	}

	@Override
	public Result filter(LogEvent event) {
		return debugMode();
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
		return debugMode();
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
		return debugMode();
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
		return debugMode();
	}
}
