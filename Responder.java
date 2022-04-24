import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author David J. Barnes and Michael Kölling.
 * @version 2016.02.29
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private static final String RESPONSE_MAP_FILE = "response.txt";

    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(RESPONSE_MAP_FILE); 
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String key = reader.readLine();
            String[] keys = key.split(", ");
            String response = reader.readLine();
            String combine = response;
            String response2 = "";
            int x = 0;
            
            while(response != null && response2 != null) {
                response2 = reader.readLine();
                if(response2 != null){
                    if(response2.trim().length() != 0){
                        combine += response2;
                    }
                    else{
                        if(combine != ""){
                            while(x < keys.length){
                                responseMap.put(keys[x],combine);
                                x++;
                            }
                            combine = "";
                            key = reader.readLine();
                            keys = key.split(", ");
                            x = 0;
                        }
                    }
                }
                
                response = reader.readLine();
                if(response != null){
                    if(response.trim().length() != 0){
                        combine += response;
                    }
                    else{
                        if(combine != ""){
                            while(x < keys.length){
                                responseMap.put(keys[x],combine);
                                x++;
                            }
                            combine = "";
                            key = reader.readLine();
                            keys = key.split(", ");
                            x = 0;
                        }
                    }
                }
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + RESPONSE_MAP_FILE);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                           RESPONSE_MAP_FILE);
        }
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES); 
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String response = reader.readLine();
            String combine = response;
            String response2 = "";
            while(response != null && response2 != null) {
                response2 = reader.readLine();
                 
                if(response2 != null){
                    if(response2.trim().length() != 0){
                        combine += response2;
                    }
                    else{
                        if(combine != ""){
                            defaultResponses.add(combine);
                            combine = "";
                        }
                    }
                }
                
                response = reader.readLine();
                if(response != null){
                    if(response.trim().length() != 0){
                        combine += response;
                    }
                    else{
                        if(combine != ""){
                            defaultResponses.add(combine);
                            combine = "";
                        }
                    }
                }
            }
            defaultResponses.add(combine);
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}