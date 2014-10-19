package constants;
public class Components {
	
	public static String getBaseFilePath() {
		String location = Components.class.getProtectionDomain().getCodeSource().getLocation().toString();
		int irrelevantFirst = location.indexOf("/Users");
		int irrelevantLast = location.indexOf("target");
		return location.substring(irrelevantFirst, irrelevantLast);
	}
	public static void main(String[] args) {
	    System.out.println(Components.getBaseFilePath());
    }
}
