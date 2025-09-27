package com.boozer.nexus.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/demo", produces = MediaType.APPLICATION_JSON_VALUE)
public class DemoController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        res.put("timestamp", Instant.now().toString());
        return res;
    }

    @PostMapping(path = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> echo(@RequestBody EchoRequest body) {
        String message = body != null && body.getMessage() != null ? body.getMessage() : "";
        Map<String, Object> res = new HashMap<>();
        res.put("message", message);
        res.put("timestamp", Instant.now().toString());
        res.put("length", message.length());
        return res;
    }

    public static class EchoRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
