// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: src/main/java/org/example/proto/message.proto
// Protobuf Java Version: 4.28.3

package org.example.kafka.proto.tutorial;

public final class KafkaMessageSample {
  private KafkaMessageSample() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 28,
      /* patch= */ 3,
      /* suffix= */ "",
      KafkaMessageSample.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_tutorial_KafkaMessage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_tutorial_KafkaMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n-src/main/java/org/example/proto/messag" +
      "e.proto\022\010tutorial\"\234\002\n\014KafkaMessage\022\n\n\002id" +
      "\030\001 \001(\t\0228\n\014request_type\030\002 \001(\0162\".tutorial." +
      "KafkaMessage.RequestType\0226\n\013methos_type\030" +
      "\003 \001(\0162!.tutorial.KafkaMessage.MethodType" +
      "\022\014\n\004body\030\004 \001(\t\022\020\n\010reply_to\030\005 \001(\t\"4\n\013Requ" +
      "estType\022\014\n\010RESPONSE\020\000\022\n\n\006NOTIFY\020\001\022\013\n\007REQ" +
      "UEST\020\003\"8\n\nMethodType\022\t\n\005CREAT\020\000\022\007\n\003GET\020\001" +
      "\022\n\n\006UPDATE\020\002\022\n\n\006DELETE\020\003B\026B\022KafkaMessage" +
      "SampleP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_tutorial_KafkaMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_tutorial_KafkaMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_tutorial_KafkaMessage_descriptor,
        new String[] { "Id", "RequestType", "MethosType", "Body", "ReplyTo", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}