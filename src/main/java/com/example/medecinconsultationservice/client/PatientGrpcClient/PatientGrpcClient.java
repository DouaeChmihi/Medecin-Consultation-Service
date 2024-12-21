package com.example.medecinconsultationservice.client.PatientGrpcClient;

import patient.PatientOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;
import patient.PatientProtoGrpc;

@Service
public class PatientGrpcClient {

    private final ManagedChannel channel;
    private final PatientProtoGrpc.PatientProtoBlockingStub blockingStub;

    public PatientGrpcClient() {
        // Configure the channel for the gRPC server
        channel = ManagedChannelBuilder.forAddress("localhost", 5065) // Server address and port
                .usePlaintext() // Disable TLS for development
                .build();

        // Create the blocking stub
        blockingStub = PatientProtoGrpc.newBlockingStub(channel);
    }

    public PatientOuterClass.Patient getPatientDetails(int id) {
        // Build request with patient ID
        PatientOuterClass.GetPatientByIdRequest request = PatientOuterClass.GetPatientByIdRequest.newBuilder()
                .setId(id)
                .build();

        // Fetch response from gRPC server
        PatientOuterClass.GetPatientByIdResponse response = blockingStub.getPatientById(request);

        if (response == null || !response.hasPatient()) {
            throw new RuntimeException("Patient not found for ID: " + id);
        }

        // Return the full patient details (not just ID, but all other fields)
        return response.getPatient();
    }
}
