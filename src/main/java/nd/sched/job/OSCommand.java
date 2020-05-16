package nd.sched.job;

public class OSCommand {
    private static String osName = System.getProperty("os.name").toLowerCase();
    private static final String[] BASH_INIT = {"bash", "-c"};
    private static final String[] CMDE_INIT = {"cmd.exe", "/c"};

    public static String[] getCommand(){
        if (isWindows()){
            return CMDE_INIT;
        }
        return BASH_INIT;
    }
	public static boolean isWindows() {
		return (osName.indexOf("win") >= 0);
	}
	public static boolean isMac() {
		return (osName.indexOf("mac") >= 0);
	}
	public static boolean isUnix() {
		return (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") >= 0 );
	}
	public static boolean isSolaris() {
		return (osName.indexOf("sunos") >= 0);
	}
	private OSCommand(){}//Only static methods here
}