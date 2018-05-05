package model;
//import application.MainInterfaceController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
//import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import marytts.modules.synthesis.Voice;
import tts.TextToSpeech;

public class SpeechCalculator {

	// Necessary
	EnglishNumberToString	numberToString	= new EnglishNumberToString();
	EnglishStringToNumber	stringToNumber	= new EnglishStringToNumber();
	TextToSpeech			textToSpeech	= new TextToSpeech();

	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());

	// Variables
	private String result;

	// Threads
	Thread	speechThread;
	Thread	resourcesThread;

	// LiveRecognizer
	private LiveSpeechRecognizer recognizer;

	private volatile boolean recognizerStopped = true;

	/**
	 * Constructor
	 */
	public SpeechCalculator() {

		// Loading Message
		logger.log(Level.INFO, "Loading..\n");

		// Configuration
		Configuration configuration = new Configuration();

		// Load model from the jar
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

		// if you want to use LanguageModelPath disable the 3 lines after which
		// are setting a custom grammar->

		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")

		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

		// Start recognition process pruning previously cached data.
		// recognizer.startRecognition(true);

		// that we have added on the class path
		Voice.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		textToSpeech.setVoice("cmu-slt-hsmm");

		// Start the Thread
		// startSpeechThread()
		recognizer.startRecognition(true);
		startResourcesThread();
	}

	/**
	 * Starting the main Thread of speech recognition
	 */
	public void startSpeechThread() {

		System.out.println("Entering start speech thread");

		// alive?
		if (speechThread != null && speechThread.isAlive())
			return;

		// initialise
		speechThread = new Thread(() -> {

			// Allocate the resources
			recognizerStopped = false;
			logger.log(Level.INFO, "You can start to speak...\n");

			try {
				while (!recognizerStopped) {
					/*
					 * This method will return when the end of speech is
					 * reached. Note that the end pointer will determine the end
					 * of speech.
					 */
//					System.out.println("line 118");
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {
//						System.out.println("line 121");
						result = speechResult.getHypothesis();
						System.out.println("You said: [" + result + "]\n");
						makeDecision(result, speechResult.getWords());
						// logger.log(Level.INFO, "You said: " + result + "\n")
					} else
						logger.log(Level.INFO, "I can't understand what you said.\n");

				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
				recognizerStopped = true;
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}

	/**
	 * Stopping the main Thread of Speech Recognition
	 */
	public void stopSpeechThread() {
		// alive?
		if (speechThread != null && speechThread.isAlive()) {
			recognizerStopped = true;
			//recognizer.stopRecognition(); it will throw error ;)
		}
	}

	/**
	 * Starting a Thread that checks if the resources needed to the
	 * SpeechRecognition library are available
	 */
	public void startResourcesThread() {

		// alive?
		if (resourcesThread != null && resourcesThread.isAlive())
			return;

		resourcesThread = new Thread(() -> {
			try {

				// Detect if the microphone is available
				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						// logger.log(Level.INFO, "Microphone is available.\n")
					} else {
						logger.log(Level.INFO, "Microphone is not available.\n");
					}

					// Sleep some period
					Thread.sleep(350);
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		// Start
		resourcesThread.start();
	}
	
//	private MainInterfaceController mainInterfaceController = new MainInterfaceController();

	/**
	 * Takes a decision based on the given result
	 * 
	 * @param speechWords
	 */
	
	int flag=0;
	public void makeDecision(String speech, List<WordResult> speechWords) {

		// Split the sentence
		// System.out.println("SpeechWords: " +
		// Arrays.toString(speechWords.toArray()))
		// if (!speech.contains("hey"))
		// return;
		// else
		// speech = speech.replace("hey", "");
		
		

		
		if (speech.contains("how are you")) {
			textToSpeech.speak("Fine Thanks", 1.5f, false, true);
			return;
		} else if (speech.contains("who is your daddy")) {
			textToSpeech.speak("You boss", 1.5f, false, true);
		} else if (speech.contains("hey boss")) {
			textToSpeech.speak("can i have the pizza pliz", 1.5f, false, true);

		} else if (speech.contains("will you obey me")) {
			textToSpeech.speak("absolutely sir", 1.5f, false, true);
			return;
		} else if (speech.contains("say hello to everyone")) {
			textToSpeech.speak("Hello Friends", 1.5f, false, true);
			return;
		} else if (speech.contains("say amazing")) {
			textToSpeech.speak("WoW it's amazing!", 1.5f, false, true);
			return;
		} else if (speech.contains("what day is today")) {
			textToSpeech.speak("A good day", 1.5f, false, true);
			return;
		} else if (speech.contains("who made you")) {
			textToSpeech.speak("I was made my rajat shukla", 1.5f, false, true);
			return;
		} else if (speech.contains("what does your boss do")) {
			textToSpeech.speak("he is an engineering student at c m r i t", 1.5f, false, true);
			return;
		} else if (speech.contains("tell the audience about the project we made")) {
			textToSpeech.speak("well! this project started in 2016 june. my components are majorly from c m u's sphinx."
					+ " i can change to 3 different voices, act as an calculator and obviously a chatbot. ha ha ha ha . "
					+ "my grammar is too small, but i am working on it. i hope you all will like me. "
					+ "thank you! have a good day!", 1.5f, false, true);
			return;
		} else if (speech.contains("what is the date")) {
		    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
		    Date date = new Date();  
//		     dt = formatter.format(date);
			textToSpeech.speak(formatter.format(date), 1.5f, false, true);
			return;
		} else if (speech.contains("play a song for me")) {
			textToSpeech.speak("Okay sir! which song? ", 1.5f, false, true);
			flag=1;
			return;
		}
//		else if(flag==1)
//		{
		else if (speech.contains("song one") && flag==1) {
		
				try {
					
						FileInputStream fis = new FileInputStream("G:/Dil Diyan Gallan.mp3");
						Player player = new Player(fis);
						player.play();
						System.out.println("Song is playing!");
//						player.close();
					}
				catch(FileNotFoundException e) {
					e.printStackTrace();
				}
				catch(JavaLayerException e) {
					e.printStackTrace();
				}
				flag=0;
			}
		
		else if (speech.contains("song two") && flag==1) {
			
			try {
				
					FileInputStream fis = new FileInputStream("G:/Closer.mp3");
					Player player = new Player(fis);
					player.play();
					System.out.println("Song is playing!");
//					player.close();
				}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			catch(JavaLayerException e) {
				e.printStackTrace();
			}
			flag=0;
		}
			
			
		
			
 
		
		else if (speech.contains("stop speaking")) {
			textToSpeech.speak("okay sir!", 1.5f, false, true);
//			MainInterfaceController mainInterfaceController = new MainInterfaceController();
//			mainInterfaceController.BorderPane.MainInterfaceController.statusLabel.setText("Status : [Stopped]");
//			infoArea.appendText("Stopping Speech Recognizer\n");
			stopSpeechThread();
			return;
		} 
		else if (speech.contains("change to voice one")) {
			textToSpeech.setVoice("cmu-slt-hsmm");
			textToSpeech.speak("Done", 1.5f, false, true);
			return;
		} else if (speech.contains("change to voice two")) {
			textToSpeech.setVoice("dfki-poppy-hsmm");
			textToSpeech.speak("Done", 1.5f, false, true);
		} else if (speech.contains("change to voice three")) {
			textToSpeech.setVoice("cmu-rms-hsmm");
			textToSpeech.speak("Done", 1.5f, false, true);
		}

		String[] array = speech.split("(plus|minus|multiply|division){1}");
		System.out.println(Arrays.toString(array) + array.length);
		// return if user said only one number
		if (array.length < 2)
			return;

		// Find the two numbers
		System.out.println("Number one is:" + stringToNumber.convert(array[0]) + " Number two is: "
				+ stringToNumber.convert(array[1]));
		int number1 = stringToNumber.convert(array[0]);// .convert(array[0])
		int number2 = stringToNumber.convert(array[1]);// .convert(array[1])

		// Calculation result in int representation
		int calculationResult = 0;
		String symbol = "?";

		// Find the mathematical symbol
		if (speech.contains("plus")) {
			calculationResult = number1 + number2;
			symbol = "+";
		} else if (speech.contains("minus")) {
			calculationResult = number1 - number2;
			symbol = "-";
		} else if (speech.contains("multiply")) {
			calculationResult = number1 * number2;
			symbol = "*";
		} else if (speech.contains("division")) {
			if (number2 == 0)
				return;
			calculationResult = number1 / number2;
			symbol = "/";
		}

		String res = numberToString.convert(Math.abs(calculationResult));

		// With words
		System.out.println("Said:[ " + speech + " ]\n\t\t which after calculation is:[ "
				+ (calculationResult >= 0 ? "" : "minus ") + res + " ] \n");

		// With numbers and math
		System.out.println("Mathematical expression:[ " + number1 + " " + symbol + " " + number2
				+ "]\n\t\t which after calculation is:[ " + calculationResult + " ]");

		// Speak Mary Speak
		textToSpeech.speak((calculationResult >= 0 ? "" : "minus ") + res, 1.5f, false, true);

	}

	// /**
	// * Java Main Application Method
	// *
	// * @param args
	// */
	// public static void main(String[] args) {
	//
	// // // Be sure that the user can't start this application by not giving
	// // the
	// // // correct entry string
	// // if (args.length == 1 && "SPEECH".equalsIgnoreCase(args[0]))
	// new Main();
	// // else
	// // Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Give me
	// // the correct entry string..");
	//
	// }

}


























//package model;
////import application.MainInterfaceController;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
////import java.time.ZonedDateTime;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Port;
//
//import edu.cmu.sphinx.api.Configuration;
//import edu.cmu.sphinx.api.LiveSpeechRecognizer;
//import edu.cmu.sphinx.api.SpeechResult;
//import edu.cmu.sphinx.result.WordResult;
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.Player;
//import marytts.modules.synthesis.Voice;
//import tts.TextToSpeech;
//
//public class SpeechCalculator {
//
//	// Necessary
//	EnglishNumberToString	numberToString	= new EnglishNumberToString();
//	EnglishStringToNumber	stringToNumber	= new EnglishStringToNumber();
//	TextToSpeech			textToSpeech	= new TextToSpeech();
//
//	// Logger
//	private Logger logger = Logger.getLogger(getClass().getName());
//
//	// Variables
//	private String result;
//
//	// Threads
//	Thread	speechThread;
//	Thread	resourcesThread;
//
//	// LiveRecognizer
//	private LiveSpeechRecognizer recognizer;
//
//	private volatile boolean recognizerStopped = true;
//
//	/**
//	 * Constructor
//	 */
//	public SpeechCalculator() {
//
//		// Loading Message
//		logger.log(Level.INFO, "Loading..\n");
//
//		// Configuration
//		Configuration configuration = new Configuration();
//
//		// Load model from the jar
//		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
//		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
//
//		// if you want to use LanguageModelPath disable the 3 lines after which
//		// are setting a custom grammar->
//
//		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")
//
//		// Grammar
//		configuration.setGrammarPath("resource:/grammars");
//		configuration.setGrammarName("grammar");
//		configuration.setUseGrammar(true);
//
//		try {
//			recognizer = new LiveSpeechRecognizer(configuration);
//		} catch (IOException ex) {
//			logger.log(Level.SEVERE, null, ex);
//		}
//
//		// Start recognition process pruning previously cached data.
//		// recognizer.startRecognition(true);
//
//		// that we have added on the class path
//		Voice.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
//		textToSpeech.setVoice("cmu-slt-hsmm");
//
//		// Start the Thread
//		// startSpeechThread()
//		recognizer.startRecognition(true);
//		startResourcesThread();
//	}
//
//	/**
//	 * Starting the main Thread of speech recognition
//	 */
//	public void startSpeechThread() {
//
//		System.out.println("Entering start speech thread");
//
//		// alive?
//		if (speechThread != null && speechThread.isAlive())
//			return;
//
//		// initialise
//		speechThread = new Thread(() -> {
//
//			// Allocate the resources
//			recognizerStopped = false;
//			logger.log(Level.INFO, "You can start to speak...\n");
//
//			try {
//				while (!recognizerStopped) {
//					/*
//					 * This method will return when the end of speech is
//					 * reached. Note that the end pointer will determine the end
//					 * of speech.
//					 */
//					System.out.println("line 118");
//					SpeechResult speechResult = recognizer.getResult();
//					if (speechResult != null) {
//						System.out.println("line 121");
//						result = speechResult.getHypothesis();
//						System.out.println("You said: [" + result + "]\n");
//						makeDecision(result, speechResult.getWords());
//						// logger.log(Level.INFO, "You said: " + result + "\n")
//					} else
//						logger.log(Level.INFO, "I can't understand what you said.\n");
//
//				}
//			} catch (Exception ex) {
//				logger.log(Level.WARNING, null, ex);
//				recognizerStopped = true;
//			}
//
//			logger.log(Level.INFO, "SpeechThread has exited...");
//		});
//
//		// Start
//		speechThread.start();
//
//	}
//
//	/**
//	 * Stopping the main Thread of Speech Recognition
//	 */
//	public void stopSpeechThread() {
//		// alive?
//		if (speechThread != null && speechThread.isAlive()) {
//			recognizerStopped = true;
//			//recognizer.stopRecognition(); it will throw error ;)
//		}
//	}
//
//	/**
//	 * Starting a Thread that checks if the resources needed to the
//	 * SpeechRecognition library are available
//	 */
//	public void startResourcesThread() {
//
//		// alive?
//		if (resourcesThread != null && resourcesThread.isAlive())
//			return;
//
//		resourcesThread = new Thread(() -> {
//			try {
//
//				// Detect if the microphone is available
//				while (true) {
//					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
//						// logger.log(Level.INFO, "Microphone is available.\n")
//					} else {
//						logger.log(Level.INFO, "Microphone is not available.\n");
//					}
//
//					// Sleep some period
//					Thread.sleep(350);
//				}
//
//			} catch (InterruptedException ex) {
//				logger.log(Level.WARNING, null, ex);
//				resourcesThread.interrupt();
//			}
//		});
//
//		// Start
//		resourcesThread.start();
//	}
//	
////	private MainInterfaceController mainInterfaceController = new MainInterfaceController();
//
//	/**
//	 * Takes a decision based on the given result
//	 * 
//	 * @param speechWords
//	 */
//	public void makeDecision(String speech, List<WordResult> speechWords) {
//
//		// Split the sentence
//		// System.out.println("SpeechWords: " +
//		// Arrays.toString(speechWords.toArray()))
//		// if (!speech.contains("hey"))
//		// return;
//		// else
//		// speech = speech.replace("hey", "");
//
//		
//		if (speech.contains("how are you")) {
//			textToSpeech.speak("Fine Thanks", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("who is your god")) {
//			textToSpeech.speak("You boss", 1.5f, false, true,0);
//		} else if (speech.contains("hey boss")) {
//			textToSpeech.speak("can i have the pizza pliz", 1.5f, false, true,0);
//
//		} else if (speech.contains("will you obey me")) {
//			textToSpeech.speak("absolutely sir", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("say hello to everyone")) {
//			textToSpeech.speak("Hello Friends", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("say amazing")) {
//			textToSpeech.speak("WoW it's amazing!", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("what day is today")) {
//			textToSpeech.speak("A good day", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("who made you")) {
//			textToSpeech.speak("I was made my rajat shukla", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("what does your boss do")) {
//			textToSpeech.speak("he is an engineering student at c m r i t", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("tell the audience about the project we made")) {
//			textToSpeech.speak("well! this project started in 2016 june. my components are majorly from c m u's sphinx."
//					+ " i can change to 3 different voices, act as an calculator and obviously a chatbot. ha ha ha ha . "
//					+ "my grammar is too small, but i am working on it. i hope you all will like me. "
//					+ "thank you! have a good day!", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("what is the date")) {
//		    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
//		    Date date = new Date();  
////		     dt = formatter.format(date);
//			textToSpeech.speak(formatter.format(date), 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("play a song for me")) {
//			textToSpeech.speak("Okay sir! which song?", 1.5f, false, true,1);
//			if (speech.contains("gayatri mantr"))
//			{
//				try {
//				
//					FileInputStream fis = new FileInputStream("G:/Gayatri Mantra.mp3");
//					Player player = new Player(fis);
//					player.play();
//					System.out.println("Song is playing!");
////					player.close();
//					}
//					catch(FileNotFoundException e) {
//						e.printStackTrace();
//					}
//					catch(JavaLayerException e) {
//						e.printStackTrace();
//					}
//				textToSpeech.speak("I hope you liked the song sir!", 1.5f, false, true,0);
//			
//				return;
//			}
//			else if (speech.contains("dil diyaa galla"))
//			{
//				try {
//					
//					FileInputStream fis = new FileInputStream("G:/Dil Diyan Gallan.mp3");
//					Player player = new Player(fis);
//					player.play();
//					System.out.println("Song is playing!");
////					player.close();
//					}
//					catch(FileNotFoundException e) {
//						e.printStackTrace();
//					}
//					catch(JavaLayerException e) {
//						e.printStackTrace();
//					}
//				textToSpeech.speak("I hope you liked the song sir!", 1.5f, false, true,0);
//			
//				return;
//			}
//			
//			
//			
//		} 
//		
//		else if (speech.contains("stop speaking")) {
//			textToSpeech.speak("okay sir!", 1.5f, false, true,0);
////			MainInterfaceController mainInterfaceController = new MainInterfaceController();
////			mainInterfaceController.BorderPane.MainInterfaceController.statusLabel.setText("Status : [Stopped]");
////			infoArea.appendText("Stopping Speech Recognizer\n");
//			stopSpeechThread();
//			return;
//		} 
//		else if (speech.contains("change to voice one")) {
//			textToSpeech.setVoice("cmu-slt-hsmm");
//			textToSpeech.speak("Done", 1.5f, false, true,0);
//			return;
//		} else if (speech.contains("change to voice two")) {
//			textToSpeech.setVoice("dfki-poppy-hsmm");
//			textToSpeech.speak("Done", 1.5f, false, true,0);
//		} else if (speech.contains("change to voice three")) {
//			textToSpeech.setVoice("cmu-rms-hsmm");
//			textToSpeech.speak("Done", 1.5f, false, true,0);
//		}
//
//		String[] array = speech.split("(plus|minus|multiply|division){1}");
//		System.out.println(Arrays.toString(array) + array.length);
//		// return if user said only one number
//		if (array.length < 2)
//			return;
//
//		// Find the two numbers
//		System.out.println("Number one is:" + stringToNumber.convert(array[0]) + " Number two is: "
//				+ stringToNumber.convert(array[1]));
//		int number1 = stringToNumber.convert(array[0]);// .convert(array[0])
//		int number2 = stringToNumber.convert(array[1]);// .convert(array[1])
//
//		// Calculation result in int representation
//		int calculationResult = 0;
//		String symbol = "?";
//
//		// Find the mathematical symbol
//		if (speech.contains("plus")) {
//			calculationResult = number1 + number2;
//			symbol = "+";
//		} else if (speech.contains("minus")) {
//			calculationResult = number1 - number2;
//			symbol = "-";
//		} else if (speech.contains("multiply")) {
//			calculationResult = number1 * number2;
//			symbol = "*";
//		} else if (speech.contains("division")) {
//			if (number2 == 0)
//				return;
//			calculationResult = number1 / number2;
//			symbol = "/";
//		}
//
//		String res = numberToString.convert(Math.abs(calculationResult));
//
//		// With words
//		System.out.println("Said:[ " + speech + " ]\n\t\t which after calculation is:[ "
//				+ (calculationResult >= 0 ? "" : "minus ") + res + " ] \n");
//
//		// With numbers and math
//		System.out.println("Mathematical expression:[ " + number1 + " " + symbol + " " + number2
//				+ "]\n\t\t which after calculation is:[ " + calculationResult + " ]");
//
//		// Speak Mary Speak
//		textToSpeech.speak((calculationResult >= 0 ? "" : "minus ") + res, 1.5f, false, true,0);
//
//	}
//
//	// /**
//	// * Java Main Application Method
//	// *
//	// * @param args
//	// */
//	// public static void main(String[] args) {
//	//
//	// // // Be sure that the user can't start this application by not giving
//	// // the
//	// // // correct entry string
//	// // if (args.length == 1 && "SPEECH".equalsIgnoreCase(args[0]))
//	// new Main();
//	// // else
//	// // Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Give me
//	// // the correct entry string..");
//	//
//	// }
//
//}