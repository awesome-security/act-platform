package no.mnemonic.act.platform.auth.properties.internal;

import no.mnemonic.commons.utilities.collections.CollectionUtils;
import no.mnemonic.commons.utilities.collections.SetUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertiesFileParserTest {

  private PropertiesFileParser parser;
  private Path propertiesFile;

  @Before
  public void setUp() {
    parser = new PropertiesFileParser();
  }

  @After
  public void cleanUp() throws Exception {
    if (propertiesFile != null) Files.deleteIfExists(propertiesFile);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsePropertiesFileNotExists() throws Exception {
    parser.parse("/non/existing/file.properties");
  }

  @Test
  public void testParsingFunctionGroup() throws Exception {
    assertFunction(parseFunctions("function.name.members = function1,function2,function3"));
  }

  @Test
  public void testParsingFunctionGroupTrimMembers() throws Exception {
    assertFunction(parseFunctions("function.name.members = function1 , function2 , function3"));
  }

  @Test
  public void testParsingFunctionGroupSkipEmptyMembers() throws Exception {
    assertFunction(parseFunctions("function.name.members = ,function1, ,function2, ,function3, "));
  }

  @Test
  public void testParsingFunctionGroupWithoutMembers() throws Exception {
    Set<Function> functions = parseFunctions("function.name.members = ");

    assertEquals(1, functions.size());
    FunctionGroup group = (FunctionGroup) functions.iterator().next();
    assertEquals("name", group.getName());
    assertTrue(CollectionUtils.isEmpty(group.getMembers()));
  }

  @Test
  public void testParsingOrganization() throws Exception {
    assertOrganization(parseOrganizations("organization.123.name = test"));
  }

  @Test
  public void testParsingOrganizationTrimName() throws Exception {
    assertOrganization(parseOrganizations("organization.123.name =    test   "));
  }

  @Test
  public void testParsingOrganizationSkipNegativeId() throws Exception {
    assertTrue(CollectionUtils.isEmpty(parseOrganizations("organization.-123.name = test")));
  }

  @Test
  public void testParsingOrganizationGroup() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group\n" +
            "organization.123.members = 1,2,3";
    assertOrganization(parseOrganizations(content));
  }

  @Test
  public void testParsingOrganizationGroupTrimMembers() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group\n" +
            "organization.123.members = 1 , 2 , 3 ";
    assertOrganization(parseOrganizations(content));
  }

  @Test
  public void testParsingOrganizationGroupSkipEmptyMembers() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group\n" +
            "organization.123.members = ,1, ,2, ,3, ";
    assertOrganization(parseOrganizations(content));
  }

  @Test
  public void testParsingOrganizationGroupSkipNegativeMembers() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group\n" +
            "organization.123.members = 1,2,3,-4,-5";
    assertOrganization(parseOrganizations(content));
  }

  @Test
  public void testParsingOrganizationGroupSkipUnparseableMembers() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group\n" +
            "organization.123.members = 1,2,3,test,wrong";
    assertOrganization(parseOrganizations(content));
  }

  @Test
  public void testParsingOrganizationGroupWithEmptyMembers() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group\n" +
            "organization.123.members = ";
    assertEmptyOrganizationGroup(parseOrganizations(content));
  }

  @Test
  public void testParsingOrganizationGroupWithoutMembers() throws Exception {
    String content = "organization.123.name = test\n" +
            "organization.123.type = group";
    assertEmptyOrganizationGroup(parseOrganizations(content));
  }

  @Test
  public void testParsingSubject() throws Exception {
    assertSubject(parseSubjects("subject.123.name = test"));
  }

  @Test
  public void testParsingSubjectTrimName() throws Exception {
    assertSubject(parseSubjects("subject.123.name =    test   "));
  }

  @Test
  public void testParsingSubjectSkipNegativeId() throws Exception {
    assertTrue(CollectionUtils.isEmpty(parseSubjects("subject.-123.name = test")));
  }

  @Test
  public void testParsingSubjectGroup() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group\n" +
            "subject.123.members = 1,2,3";
    assertSubject(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectGroupTrimMembers() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group\n" +
            "subject.123.members = 1 , 2 , 3 ";
    assertSubject(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectGroupSkipEmptyMembers() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group\n" +
            "subject.123.members = ,1, ,2, ,3, ";
    assertSubject(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectGroupSkipNegativeMembers() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group\n" +
            "subject.123.members = 1,2,3,-4,-5";
    assertSubject(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectGroupSkipUnparseableMembers() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group\n" +
            "subject.123.members = 1,2,3,test,wrong";
    assertSubject(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectGroupWithEmptyMembers() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group\n" +
            "subject.123.members = ";
    assertEmptySubjectGroup(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectGroupWithoutMembers() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.type = group";
    assertEmptySubjectGroup(parseSubjects(content));
  }

  @Test
  public void testParsingSubjectPermissions() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.permission.1 = function1,function2,function3\n" +
            "subject.123.permission.2 = function1 , function2 , function3\n" +
            "subject.123.permission.3 = ,function1, ,function2, ,function3, ";

    Subject subject = parseSubjects(content).iterator().next();
    assertEquals(3, subject.getPermissions().size());
    for (long i = 1; i <= subject.getPermissions().size(); i++) {
      assertEquals(SetUtils.set("function1", "function2", "function3"), subject.getPermissions().get(i));
    }
  }

  @Test
  public void testParsingSubjectPermissionsWithoutFunctions() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.permission.1 = ";

    Subject subject = parseSubjects(content).iterator().next();
    assertEquals(1, subject.getPermissions().size());
    assertTrue(CollectionUtils.isEmpty(subject.getPermissions().get(1L)));
  }

  @Test
  public void testParsingSubjectPermissionsSkipNegativeOrganizationId() throws Exception {
    String content = "subject.123.name = test\n" +
            "subject.123.permission.-1 = function1,function2,function3";

    Subject subject = parseSubjects(content).iterator().next();
    assertEquals(0, subject.getPermissions().size());
  }

  private Set<Function> parseFunctions(String content) throws Exception {
    createPropertiesFile(content);
    parser.parse(propertiesFile.toString());
    return parser.getFunctions();
  }

  private Set<Organization> parseOrganizations(String content) throws Exception {
    createPropertiesFile(content);
    parser.parse(propertiesFile.toString());
    return parser.getOrganizations();
  }

  private Set<Subject> parseSubjects(String content) throws Exception {
    createPropertiesFile(content);
    parser.parse(propertiesFile.toString());
    return parser.getSubjects();
  }

  private void createPropertiesFile(String content) throws Exception {
    propertiesFile = Files.createTempFile(UUID.randomUUID().toString(), ".properties");
    try (FileWriter writer = new FileWriter(propertiesFile.toFile())) {
      writer.write(content);
    }
  }

  private void assertFunction(Set<Function> functions) {
    assertEquals(1, functions.size());
    Function function = functions.iterator().next();
    assertEquals("name", function.getName());

    if (function.isGroup()) {
      assertEquals(SetUtils.set("function1", "function2", "function3"), FunctionGroup.class.cast(function).getMembers());
    }
  }

  private void assertOrganization(Set<Organization> organizations) {
    assertEquals(1, organizations.size());
    Organization organization = organizations.iterator().next();
    assertEquals("test", organization.getName());
    assertEquals(123, organization.getInternalID());

    if (organization.isGroup()) {
      assertEquals(SetUtils.set(1L, 2L, 3L), OrganizationGroup.class.cast(organization).getMembers());
    }
  }

  private void assertEmptyOrganizationGroup(Set<Organization> organizations) {
    assertEquals(1, organizations.size());
    OrganizationGroup group = (OrganizationGroup) organizations.iterator().next();
    assertEquals("test", group.getName());
    assertEquals(123, group.getInternalID());
    assertTrue(CollectionUtils.isEmpty(group.getMembers()));
  }

  private void assertSubject(Set<Subject> subjects) {
    assertEquals(1, subjects.size());
    Subject subject = subjects.iterator().next();
    assertEquals("test", subject.getName());
    assertEquals(123, subject.getInternalID());

    if (subject.isGroup()) {
      assertEquals(SetUtils.set(1L, 2L, 3L), SubjectGroup.class.cast(subject).getMembers());
    }
  }

  private void assertEmptySubjectGroup(Set<Subject> subjects) {
    assertEquals(1, subjects.size());
    SubjectGroup group = (SubjectGroup) subjects.iterator().next();
    assertEquals("test", group.getName());
    assertEquals(123, group.getInternalID());
    assertTrue(CollectionUtils.isEmpty(group.getMembers()));
  }

}