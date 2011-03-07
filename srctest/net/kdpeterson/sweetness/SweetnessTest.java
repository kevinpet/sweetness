package net.kdpeterson.sweetness;

import static org.junit.Assert.assertEquals;
import google.protobuf.compiler.Plugin.CodeGeneratorRequest;
import google.protobuf.compiler.Plugin.CodeGeneratorResponse;
import google.protobuf.compiler.Plugin.CodeGeneratorResponse.File;

import java.io.IOException;

import net.kdpeterson.sweetness.Sweetness;

import org.junit.Test;

public class SweetnessTest {
  @Test
  public void testGenerate() throws IOException {
    CodeGeneratorRequest request = CodeGeneratorRequest
        .parseFrom(SweetnessTest.class
            .getResourceAsStream("/code-generator-request.message"));
    CodeGeneratorResponse response = new Sweetness().generate(request);
    assertEquals(4, response.getFileCount());
    File color1 = response.getFile(0);
    assertEquals("kevin/Messages.java", color1.getName());
    assertEquals("enum_scope:kevin.Before.Color", color1.getInsertionPoint());
    File direction1 = response.getFile(1);
    assertEquals("enum_scope:kevin.Before.Direction", direction1.getInsertionPoint());
    File color2 = response.getFile(2);
    assertEquals("enum_scope:kevin.After.Color", color2.getInsertionPoint());
    File direction2 = response.getFile(3);
    assertEquals("enum_scope:kevin.After.Direction", direction2.getInsertionPoint());
  }

  @Test
  public void testGetFilename() {
    String filename = new Sweetness().getJavaFilename("com.foo.bar", "my_protos.proto");
    assertEquals("com/foo/bar/MyProtos.java", filename);
  }
}
