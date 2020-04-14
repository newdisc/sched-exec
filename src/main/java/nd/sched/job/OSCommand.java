package nd.sched.job;

public class OSCommand {
    private static String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final String[] BASH_INIT = {"bash", "-c"};
    private static final String[] CMDE_INIT = {"cmd.exe", "/c"};

    public static String[] getCommand(){
        if (isWindows()){
            return CMDE_INIT;
        }
        return BASH_INIT;
    }
	public static boolean isWindows() {
		return (OS_NAME.indexOf("win") >= 0);
	}
	public static boolean isMac() {
		return (OS_NAME.indexOf("mac") >= 0);
	}
	public static boolean isUnix() {
		return (OS_NAME.indexOf("nix") >= 0 || OS_NAME.indexOf("nux") >= 0 || OS_NAME.indexOf("aix") > 0 );
	}
	public static boolean isSolaris() {
		return (OS_NAME.indexOf("sunos") >= 0);
	}
}