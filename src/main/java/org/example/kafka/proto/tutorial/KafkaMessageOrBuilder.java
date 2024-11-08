// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: src/main/java/org/example/kafka/proto/message.proto
// Protobuf Java Version: 4.28.3

package org.example.kafka.proto.tutorial;

public interface KafkaMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tutorial.KafkaMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string correlationId = 1;</code>
   * @return The correlationId.
   */
  java.lang.String getCorrelationId();
  /**
   * <code>string correlationId = 1;</code>
   * @return The bytes for correlationId.
   */
  com.google.protobuf.ByteString
      getCorrelationIdBytes();

  /**
   * <code>.tutorial.KafkaMessage.RequestType requestType = 2;</code>
   * @return The enum numeric value on the wire for requestType.
   */
  int getRequestTypeValue();
  /**
   * <code>.tutorial.KafkaMessage.RequestType requestType = 2;</code>
   * @return The requestType.
   */
  KafkaMessage.RequestType getRequestType();

  /**
   * <code>.tutorial.KafkaMessage.RequestResult requestResult = 3;</code>
   * @return The enum numeric value on the wire for requestResult.
   */
  int getRequestResultValue();
  /**
   * <code>.tutorial.KafkaMessage.RequestResult requestResult = 3;</code>
   * @return The requestResult.
   */
  KafkaMessage.RequestResult getRequestResult();

  /**
   * <code>string body = 4;</code>
   * @return The body.
   */
  java.lang.String getBody();
  /**
   * <code>string body = 4;</code>
   * @return The bytes for body.
   */
  com.google.protobuf.ByteString
      getBodyBytes();

  /**
   * <code>string reply_to = 5;</code>
   * @return The replyTo.
   */
  java.lang.String getReplyTo();
  /**
   * <code>string reply_to = 5;</code>
   * @return The bytes for replyTo.
   */
  com.google.protobuf.ByteString
      getReplyToBytes();
}
