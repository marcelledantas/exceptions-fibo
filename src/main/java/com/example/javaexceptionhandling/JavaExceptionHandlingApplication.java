package com.example.javaexceptionhandling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@RequestMapping("fibonacci")
public class JavaExceptionHandlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaExceptionHandlingApplication.class, args);
    }

    @GetMapping("findRatio")
    public ResponseEntity<String> getRatio(@RequestParam int n) {

        int result;
        try{
            int dividend = fibonacci(n);
            int divisor = fibonacci(n-1);
            if(divisor == 0){
                return ResponseEntity.ok("0");
            }

            result = dividend/divisor;

        } catch(FibonacciOutOfRangeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (ArithmeticException e) {
            return ResponseEntity.ok("0");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reach out to our support");
        }
        return ResponseEntity.ok(String.valueOf(result));
    }
    @GetMapping("findNumber")
    public ResponseEntity<String> findFibonacciNumber(@RequestParam int n){
        int r;
        try{
            r = fibonacci(n);
        } catch (FibonacciOutOfRangeException e){ //Specific exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e){ // Generics exception
            //Logging and metrics: learn more about this concepts
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reach out to support me@support.com");
        }
        return ResponseEntity.ok(String.valueOf(r));
    }

    /**
     * Recursively find the Fibonacci number at the position in the sequence
     *
     * @param position requested position of fibonacci sequence (i.e. 8th number in sequence)
     * @return fibonacci number at position (i.e. 21)
     */
    private int fibonacci(int position) throws FibonacciOutOfRangeException {
        if(position <= 1){
            return position;
        }
        if(position >= 8){
            throw new FibonacciOutOfRangeException(String.format("Requested position %s is too large. Please try again.", position));
        }

        return fibonacci(position - 1) + fibonacci(position - 2);
    }

    @PostMapping("createSequence")
    public ResponseEntity<String> generateFibonacciSequence(@RequestParam String n){

        List<Integer> sequence;

        try{
           sequence = getSequence(n, null);
        } catch (FibonacciInputException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("NPE");
        }
        String filename;

        try{
            filename = storeSequence(sequence);
        } catch(IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reach out to our support");
        }
        return ResponseEntity.ok(filename);
    }

    @GetMapping("getSequence")
    public ResponseEntity<String> retrieveFibonacciSequence(@RequestParam String filename){

        String sequence;

        try{
            sequence = getSequenceByFileName(filename);
        }
        catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not available. Please check request and try again " + e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reach out to our support");
        }

        return ResponseEntity.ok(sequence);
    }

    private String getSequenceByFileName(String filename) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return reader.lines().collect(Collectors.joining());
    }

    private String storeSequence(List<Integer> sequence) throws IOException {
        String name = "fibonacci.txt";
        File file = new File(name);

        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(sequence.toString());
        writer.flush();
        writer.close();

        return name;
    }

    private List<Integer> getSequence(String str, List<Integer> sequence) throws FibonacciInputException{
        int n;
        try{
            n = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new FibonacciInputException("Invalid input. Please provide a valid number");
        }

//        List<Integer> sequence = null;
        if(sequence == null){
            sequence = new ArrayList<>();
        }

        sequence.add(0);
        int prev = 0;
        int curr = 1;
        int index = 1;

        while(index<= n){
            sequence.add(curr);
            int next = prev + curr;
            prev = curr;
            curr = next;
            index++;
        }
        return sequence;
    }

}
