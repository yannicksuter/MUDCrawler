package com.ndsi.mudcrawler.text;

public final class ANSI {

	public static final String SANE = "\u001B[0m";

	public static final String ITALIC = "\u001B[3m";
	public static final String UNDERLINE = "\u001B[4m";
	public static final String BLINK = "\u001B[5m";
	public static final String RAPID_BLINK = "\u001B[6m";
	public static final String REVERSE_VIDEO = "\u001B[7m";
	public static final String INVISIBLE_TEXT = "\u001B[8m";

	public static final String BLACK = "\033[0;30m";
	public static final String BLUE  = "\033[0;34m";
	public static final String GREEN = "\033[0;32m";
	public static final String CYAN = "\033[0;36m";
	public static final String RED = "\033[0;31m";
	public static final String PURPLE = "\033[0;35m";
	public static final String BROWN = "\033[0;33m";
	public static final String GREY = "\033[0;37m";
	public static final String DARK_GREY = "\033[1;30m";
	public static final String LIGHT_BLUE = "\033[1;34m";
	public static final String LIGHT_GREEN = "\033[1;32m";
	public static final String LIGHT_CYAN = "\033[1;36m";
	public static final String LIGHT_RED = "\033[1;31m";
	public static final String LIGHT_PURPLE = "\033[1;35m";
	public static final String YELLOW = "\033[1;33m";
	public static final String WHITE = "\033[1;37m";

	public static final String BACKGROUND_BLACK = "\u001B[40m";
	public static final String BACKGROUND_RED = "\u001B[41m";
	public static final String BACKGROUND_GREEN = "\u001B[42m";
	public static final String BACKGROUND_YELLOW = "\u001B[43m";
	public static final String BACKGROUND_BLUE = "\u001B[44m";
	public static final String BACKGROUND_MAGENTA = "\u001B[45m";
	public static final String BACKGROUND_CYAN = "\u001B[46m";
	public static final String BACKGROUND_WHITE = "\u001B[47m";

	private ANSI() {
	}

}