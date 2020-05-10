package fbcmd4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "facebook4j.properties";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("Starting app!");
		Facebook facebook =  null;
		Properties props = null;
		int option = 0;
		
		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}
		try {
			Scanner scan = new Scanner(System.in);
			while(true) {
				facebook = Utils.configFacebook(props);
				System.out.println("Java Facebook client \n\n"
								+  "Opcions: \n"
								+  "[0] Configure your client \n"
								+  "[1] NewsFeed \n"
								+  "[2] Wall \n"
								+  "[3] Publish status \n"
								+  "[4] Publish link \n"
								+  "[5] Exit");
				try {
					option = scan.nextInt();
					scan.nextLine();
					switch (option) {
					case 0:
						Utils.configTokens(CONFIG_DIR, CONFIG_FILE, props, scan);
						props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
						break;
					case 1:
						System.out.println("Let's see your newsfeed...");
						ResponseList<Post> newsFeed = facebook.getFeed();
						for (Post p : newsFeed) {
							Utils.printPost(p);
						}
						askToSaveFile("NewsFeed", newsFeed, scan);
						break;
					case 2:
						System.out.println("Let's see your wall...");
						ResponseList<Post> wall = facebook.getPosts();
						for (Post p : wall) {
							Utils.printPost(p);
						}		
						askToSaveFile("Wall", wall, scan);
						break;
					case 3:
						System.out.println("Write your status: ");
						String status = scan.nextLine();
						Utils.postStatus(status, facebook);
						break;
					case 4:
						System.out.println(": ");
						String link = scan.nextLine();
						Utils.postLink(link, facebook);
						break;
					case 5:
						System.out.println("Thank you very much for using the client!");
						logger.info("Exiting!");
						System.exit(0);
						break;
					default:
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("Error Occured, check the logs.");
					logger.error("Option is invalid. %s. \n", ex.getClass());
				} catch (FacebookException ex) {
					System.out.println("Error Occured, check the logs.");
					logger.error(ex.getErrorMessage());
				} catch (Exception ex) {
					System.out.println("Error Occured, check the logs.");
					logger.error(ex);
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	public static void askToSaveFile(String fileName, ResponseList<Post> posts, Scanner scan) {
		System.out.println("Save your wall feed? [Y]/[N]");
		String option = scan.nextLine();
		
		if (option.contains("Y") || option.contains("y")) {
			List<Post> ps = new ArrayList<>();
			int n = 0;

			while(n <= 0) {
				try {
					System.out.println("How many posts do you want to save?");
					n = Integer.parseInt(scan.nextLine());					
			
					if(n <= 0) {
						System.out.println("Please insert a valid number");
					} else {
						for(int i = 0; i<n; i++) {
							if(i>posts.size()-1) break;
							ps.add(posts.get(i));
						}
					}
				} catch(NumberFormatException e) {
					logger.error(e);
				}
			}

			Utils.savePostsToFile(fileName, ps);
		}
	}

}
