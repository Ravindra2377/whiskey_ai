package com.boozer.nexus.quantum.utils;

import com.boozer.nexus.quantum.models.*;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Quantum Circuit Builder
 * 
 * Utility for building quantum circuits for various algorithms.
 */
@Component
public class QuantumCircuitBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(QuantumCircuitBuilder.class);
    
    /**
     * Build QAOA circuit for optimization problems
     */
    public QuantumCircuit buildQAOACircuit(int numQubits, int layers, double[] beta, double[] gamma, Map<String, Double> problemHamiltonian) {
        logger.debug("Building QAOA circuit with {} qubits, {} layers", numQubits, layers);
        
        QuantumCircuit circuit = new QuantumCircuit();
        circuit.setId(UUID.randomUUID().toString());
        circuit.setNumQubits(numQubits);
        circuit.setGates(new ArrayList<>());
        
        // Initialize superposition
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
        }
        
        // QAOA layers
        for (int layer = 0; layer < layers; layer++) {
            // Problem Hamiltonian (Cost function)
            addProblemHamiltonian(circuit, problemHamiltonian, gamma[layer]);
            
            // Mixer Hamiltonian
            addMixerHamiltonian(circuit, numQubits, beta[layer]);
        }
        
        // Final measurements
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("MEASURE", Arrays.asList(i), 0.0));
        }
        
        circuit.setDepth(calculateCircuitDepth(circuit));
        return circuit;
    }
    
    /**
     * Build Grover's search circuit
     */
    public QuantumCircuit buildGroverCircuit(int numQubits, List<String> targetStates, int iterations) {
        logger.debug("Building Grover circuit with {} qubits, {} iterations", numQubits, iterations);
        
        QuantumCircuit circuit = new QuantumCircuit();
        circuit.setId(UUID.randomUUID().toString());
        circuit.setNumQubits(numQubits + 1); // +1 for ancilla
        circuit.setGates(new ArrayList<>());
        
        // Initialize superposition on all qubits except ancilla
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
        }
        
        // Initialize ancilla in |1⟩ state for phase kickback
        circuit.getGates().add(createGate("X", Arrays.asList(numQubits), 0.0));
        circuit.getGates().add(createGate("H", Arrays.asList(numQubits), 0.0));
        
        // Grover iterations
        for (int iter = 0; iter < iterations; iter++) {
            // Oracle
            addGroverOracle(circuit, numQubits, targetStates);
            
            // Diffusion operator
            addGroverDiffusion(circuit, numQubits);
        }
        
        // Measurements on data qubits
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("MEASURE", Arrays.asList(i), 0.0));
        }
        
        circuit.setDepth(calculateCircuitDepth(circuit));
        return circuit;
    }
    
    /**
     * Build Variational Quantum Classifier (VQC) circuit
     */
    public QuantumCircuit buildVQCCircuit(int numQubits, int numLayers, double[] parameters, List<Double> inputData) {
        logger.debug("Building VQC circuit with {} qubits, {} layers", numQubits, numLayers);
        
        QuantumCircuit circuit = new QuantumCircuit();
        circuit.setId(UUID.randomUUID().toString());
        circuit.setNumQubits(numQubits);
        circuit.setGates(new ArrayList<>());
        
        // Data encoding
        addDataEncoding(circuit, inputData, numQubits);
        
        // Variational layers
        int paramIndex = 0;
        for (int layer = 0; layer < numLayers; layer++) {
            // Rotation gates on each qubit
            for (int i = 0; i < numQubits; i++) {
                circuit.getGates().add(createGate("RY", Arrays.asList(i), parameters[paramIndex++]));
                circuit.getGates().add(createGate("RZ", Arrays.asList(i), parameters[paramIndex++]));
            }
            
            // Entangling gates
            for (int i = 0; i < numQubits - 1; i++) {
                circuit.getGates().add(createGate("CNOT", Arrays.asList(i, i + 1), 0.0));
            }
            
            // Additional rotation layer
            for (int i = 0; i < numQubits; i++) {
                circuit.getGates().add(createGate("RY", Arrays.asList(i), parameters[paramIndex++]));
            }
        }
        
        // Measurements
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("MEASURE", Arrays.asList(i), 0.0));
        }
        
        circuit.setDepth(calculateCircuitDepth(circuit));
        return circuit;
    }
    
    /**
     * Build Quantum Machine Learning circuit
     */
    public QuantumCircuit buildQMLCircuit(List<Double> features, double[] weights, String circuitType) {
        logger.debug("Building QML circuit of type: {}", circuitType);
        
        int numQubits = features.size();
        QuantumCircuit circuit = new QuantumCircuit();
        circuit.setId(UUID.randomUUID().toString());
        circuit.setNumQubits(numQubits);
        circuit.setGates(new ArrayList<>());
        
        switch (circuitType.toLowerCase()) {
            case "angle_encoding":
                addAngleEncoding(circuit, features);
                break;
            case "amplitude_encoding":
                addAmplitudeEncoding(circuit, features);
                break;
            case "basis_encoding":
                addBasisEncoding(circuit, features);
                break;
            default:
                addAngleEncoding(circuit, features); // Default
        }
        
        // Add variational layers with weights
        addVariationalLayer(circuit, weights, numQubits);
        
        // Measurements
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("MEASURE", Arrays.asList(i), 0.0));
        }
        
        circuit.setDepth(calculateCircuitDepth(circuit));
        return circuit;
    }
    
    /**
     * Build Quantum Simulation circuit
     */
    public QuantumCircuit buildSimulationCircuit(String hamiltonian, double time, int trotterSteps) {
        logger.debug("Building simulation circuit for Hamiltonian: {}", hamiltonian);
        
        // Parse Hamiltonian and determine number of qubits
        int numQubits = parseHamiltonianQubits(hamiltonian);
        
        QuantumCircuit circuit = new QuantumCircuit();
        circuit.setId(UUID.randomUUID().toString());
        circuit.setNumQubits(numQubits);
        circuit.setGates(new ArrayList<>());
        
        // Initial state preparation
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
        }
        
        // Trotter decomposition
        double stepTime = time / trotterSteps;
        for (int step = 0; step < trotterSteps; step++) {
            addTrotterStep(circuit, hamiltonian, stepTime);
        }
        
        // Measurements
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("MEASURE", Arrays.asList(i), 0.0));
        }
        
        circuit.setDepth(calculateCircuitDepth(circuit));
        return circuit;
    }
    
    /**
     * Build Quantum Fourier Transform circuit
     */
    public QuantumCircuit buildQFTCircuit(int numQubits, boolean inverse) {
        logger.debug("Building {}QFT circuit with {} qubits", inverse ? "inverse " : "", numQubits);
        
        QuantumCircuit circuit = new QuantumCircuit();
        circuit.setId(UUID.randomUUID().toString());
        circuit.setNumQubits(numQubits);
        circuit.setGates(new ArrayList<>());
        
        if (!inverse) {
            // Forward QFT
            for (int i = 0; i < numQubits; i++) {
                circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
                
                for (int j = i + 1; j < numQubits; j++) {
                    double angle = Math.PI / Math.pow(2, j - i);
                    addControlledRotation(circuit, j, i, angle);
                }
            }
        } else {
            // Inverse QFT
            for (int i = numQubits - 1; i >= 0; i--) {
                for (int j = numQubits - 1; j > i; j--) {
                    double angle = -Math.PI / Math.pow(2, j - i);
                    addControlledRotation(circuit, j, i, angle);
                }
                
                circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
            }
        }
        
        // Swap qubits to reverse order
        for (int i = 0; i < numQubits / 2; i++) {
            addSwapGate(circuit, i, numQubits - 1 - i);
        }
        
        circuit.setDepth(calculateCircuitDepth(circuit));
        return circuit;
    }
    
    // Helper methods
    
    private QuantumGate createGate(String type, List<Integer> qubits, double parameter) {
        QuantumGate gate = new QuantumGate();
        gate.setType(type);
        gate.setQubits(qubits);
        gate.setParameter(parameter);
        return gate;
    }
    
    private void addProblemHamiltonian(QuantumCircuit circuit, Map<String, Double> hamiltonian, double gamma) {
        for (Map.Entry<String, Double> term : hamiltonian.entrySet()) {
            String[] qubits = term.getKey().split(",");
            double coefficient = term.getValue() * gamma;
            
            if (qubits.length == 1) {
                // Single qubit term
                int qubit = Integer.parseInt(qubits[0].trim());
                circuit.getGates().add(createGate("RZ", Arrays.asList(qubit), 2 * coefficient));
            } else if (qubits.length == 2) {
                // Two qubit term
                int qubit1 = Integer.parseInt(qubits[0].trim());
                int qubit2 = Integer.parseInt(qubits[1].trim());
                circuit.getGates().add(createGate("ZZ", Arrays.asList(qubit1, qubit2), coefficient));
            }
        }
    }
    
    private void addMixerHamiltonian(QuantumCircuit circuit, int numQubits, double beta) {
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("RX", Arrays.asList(i), 2 * beta));
        }
    }
    
    private void addGroverOracle(QuantumCircuit circuit, int numQubits, List<String> targetStates) {
        // Simplified oracle - marks target states with phase flip
        for (String target : targetStates) {
            List<Integer> flipQubits = new ArrayList<>();
            
            // Prepare state for multi-controlled Z
            for (int i = 0; i < numQubits; i++) {
                if (target.charAt(i) == '0') {
                    circuit.getGates().add(createGate("X", Arrays.asList(i), 0.0));
                    flipQubits.add(i);
                }
            }
            
            // Multi-controlled Z gate
            List<Integer> allQubits = IntStream.range(0, numQubits).boxed().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            circuit.getGates().add(createGate("MCZ", allQubits, 0.0));
            
            // Undo X gates
            for (int qubit : flipQubits) {
                circuit.getGates().add(createGate("X", Arrays.asList(qubit), 0.0));
            }
        }
    }
    
    private void addGroverDiffusion(QuantumCircuit circuit, int numQubits) {
        // Diffusion operator: 2|s⟩⟨s| - I
        
        // H gates
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
        }
        
        // X gates
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("X", Arrays.asList(i), 0.0));
        }
        
        // Multi-controlled Z
        List<Integer> allQubits = IntStream.range(0, numQubits).boxed().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        circuit.getGates().add(createGate("MCZ", allQubits, 0.0));
        
        // X gates
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("X", Arrays.asList(i), 0.0));
        }
        
        // H gates
        for (int i = 0; i < numQubits; i++) {
            circuit.getGates().add(createGate("H", Arrays.asList(i), 0.0));
        }
    }
    
    private void addDataEncoding(QuantumCircuit circuit, List<Double> inputData, int numQubits) {
        // Angle encoding
        for (int i = 0; i < Math.min(inputData.size(), numQubits); i++) {
            double angle = inputData.get(i) * Math.PI; // Scale to [0, π]
            circuit.getGates().add(createGate("RY", Arrays.asList(i), angle));
        }
    }
    
    private void addAngleEncoding(QuantumCircuit circuit, List<Double> features) {
        for (int i = 0; i < features.size(); i++) {
            double angle = features.get(i) * Math.PI;
            circuit.getGates().add(createGate("RY", Arrays.asList(i), angle));
        }
    }
    
    private void addAmplitudeEncoding(QuantumCircuit circuit, List<Double> features) {
        // Simplified amplitude encoding using rotation gates
        int numQubits = circuit.getNumQubits();
        
        // Normalize features
        double norm = Math.sqrt(features.stream().mapToDouble(x -> x * x).sum());
        List<Double> normalized = features.stream().map(x -> x / norm).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        // Use rotation gates to encode amplitudes
        for (int i = 0; i < Math.min(normalized.size(), numQubits); i++) {
            double angle = 2 * Math.asin(Math.abs(normalized.get(i)));
            circuit.getGates().add(createGate("RY", Arrays.asList(i), angle));
        }
    }
    
    private void addBasisEncoding(QuantumCircuit circuit, List<Double> features) {
        // Binary encoding of features
        for (int i = 0; i < features.size(); i++) {
            if (features.get(i) > 0.5) { // Threshold at 0.5
                circuit.getGates().add(createGate("X", Arrays.asList(i), 0.0));
            }
        }
    }
    
    private void addVariationalLayer(QuantumCircuit circuit, double[] weights, int numQubits) {
        int weightIndex = 0;
        
        // Rotation gates
        for (int i = 0; i < numQubits && weightIndex < weights.length; i++) {
            circuit.getGates().add(createGate("RY", Arrays.asList(i), weights[weightIndex++]));
            if (weightIndex < weights.length) {
                circuit.getGates().add(createGate("RZ", Arrays.asList(i), weights[weightIndex++]));
            }
        }
        
        // Entangling gates
        for (int i = 0; i < numQubits - 1; i++) {
            circuit.getGates().add(createGate("CNOT", Arrays.asList(i, i + 1), 0.0));
        }
    }
    
    private int parseHamiltonianQubits(String hamiltonian) {
        // Simple parser - assumes format like "Z0 + X1 + Z0*Z1"
        Set<Integer> qubits = new HashSet<>();
        String[] terms = hamiltonian.split("\\+");
        
        for (String term : terms) {
            String[] factors = term.split("\\*");
            for (String factor : factors) {
                String trimmed = factor.trim();
                if (trimmed.length() > 1) {
                    char qubitChar = trimmed.charAt(trimmed.length() - 1);
                    if (Character.isDigit(qubitChar)) {
                        qubits.add(Character.getNumericValue(qubitChar));
                    }
                }
            }
        }
        
        return qubits.isEmpty() ? 1 : Collections.max(qubits) + 1;
    }
    
    private void addTrotterStep(QuantumCircuit circuit, String hamiltonian, double stepTime) {
        // Simplified Trotter step - apply evolution for each Pauli term
        String[] terms = hamiltonian.split("\\+");
        
        for (String term : terms) {
            String trimmed = term.trim();
            if (trimmed.startsWith("Z")) {
                int qubit = Character.getNumericValue(trimmed.charAt(1));
                circuit.getGates().add(createGate("RZ", Arrays.asList(qubit), 2 * stepTime));
            } else if (trimmed.startsWith("X")) {
                int qubit = Character.getNumericValue(trimmed.charAt(1));
                circuit.getGates().add(createGate("RX", Arrays.asList(qubit), 2 * stepTime));
            } else if (trimmed.startsWith("Y")) {
                int qubit = Character.getNumericValue(trimmed.charAt(1));
                circuit.getGates().add(createGate("RY", Arrays.asList(qubit), 2 * stepTime));
            }
        }
    }
    
    private void addControlledRotation(QuantumCircuit circuit, int control, int target, double angle) {
        // Implement controlled rotation using decomposition
        circuit.getGates().add(createGate("RZ", Arrays.asList(target), angle / 2));
        circuit.getGates().add(createGate("CNOT", Arrays.asList(control, target), 0.0));
        circuit.getGates().add(createGate("RZ", Arrays.asList(target), -angle / 2));
        circuit.getGates().add(createGate("CNOT", Arrays.asList(control, target), 0.0));
    }
    
    private void addSwapGate(QuantumCircuit circuit, int qubit1, int qubit2) {
        // SWAP = CNOT(1,2) + CNOT(2,1) + CNOT(1,2)
        circuit.getGates().add(createGate("CNOT", Arrays.asList(qubit1, qubit2), 0.0));
        circuit.getGates().add(createGate("CNOT", Arrays.asList(qubit2, qubit1), 0.0));
        circuit.getGates().add(createGate("CNOT", Arrays.asList(qubit1, qubit2), 0.0));
    }
    
    private int calculateCircuitDepth(QuantumCircuit circuit) {
        // Simplified depth calculation - count layers
        return circuit.getGates().size(); // Simplified - assumes serial execution
    }
}