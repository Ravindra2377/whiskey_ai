package com.boozer.nexus.cli.model;

public class OperationDescriptor {
    public String id;
    public String name;
    public String type; // java|json|script
    public String path;
    public String summary;
    public java.util.List<String> tags;
}
