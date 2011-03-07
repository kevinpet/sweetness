package net.kdpeterson.sweetness;

import static java.lang.String.format;
import google.protobuf.compiler.Plugin;
import google.protobuf.compiler.Plugin.CodeGeneratorRequest;
import google.protobuf.compiler.Plugin.CodeGeneratorResponse;
import google.protobuf.compiler.Plugin.CodeGeneratorResponse.File;

import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

public class Sweetness {

  public static void main(String[] args) throws IOException {
    new Sweetness().run();
  }

  void run() throws IOException {
    InputStream is = System.in;
    CodeGeneratorRequest request = Plugin.CodeGeneratorRequest.parseFrom(is);
    CodeGeneratorResponse response = generate(request);
    response.writeTo(System.out);
  }

  CodeGeneratorResponse generate(CodeGeneratorRequest request) {
    CodeGeneratorResponse.Builder builder = CodeGeneratorResponse.newBuilder();
    for (FileDescriptorProto file : request.getProtoFileList()) {
      String javaFilename = getJavaFilename(file.getPackage(), file.getName());
      for (DescriptorProto msg : file.getMessageTypeList()) {
        for (EnumDescriptorProto en : msg.getEnumTypeList()) {
          File.Builder f = File.newBuilder();
          f.setName(javaFilename);
          f.setInsertionPoint(format("enum_scope:%s.%s.%s", file.getPackage(), msg.getName(), en.getName()));
          f.setContent(getEnumVisitor(en));
          builder.addFile(f);
        }
      }
      // insert visitors at outer_class_scope
    }
    return builder.build();
  }

  private String getEnumVisitor(EnumDescriptorProto en) {
    return format("// create a %sVisitor", en.getName());
  }

  String getJavaFilename(String packageName, String protoName) {
    String dir = packageName.replaceAll("\\.", "/");
    String base = protoName.replaceAll("\\.proto$", "").replaceAll(".*\\/", "");
    char[] chars = base.toCharArray();
    StringBuilder sb = new StringBuilder();
    boolean start = true;
    for (char c : chars) {
      if (c == '_') {
        start = true;
        continue;
      }
      if (start) {
        sb.append(Character.toUpperCase(c));
      } else {
        sb.append(c);
      }
      start = false;
    }
    return dir + "/" + sb.toString() + ".java";
  }
}
