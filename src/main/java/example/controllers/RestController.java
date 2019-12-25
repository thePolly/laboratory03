package example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RestController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ResourceLoader loader;

    private static final String SERVER_URI = "http://localhost:8082/rest/";

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String watchIndex(Model model){
        return "index";
    }


    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public String watchGroups(Model model) throws TransformerException {
        String element = HttpRequestHelper.getHttp("groups", SERVER_URI);
        Document document = HttpRequestHelper.getDocument(element);
        model.addAttribute("groups", document);
        return "groups";
    }

    @RequestMapping(value = "/groupsByLesson", method = RequestMethod.GET)
    public String watchGroupsByLesson(Model model, @RequestParam("id") String id){
        String element = HttpRequestHelper.getHttp("groupsByLesson", SERVER_URI);
        Document document = HttpRequestHelper.getDocument(element);
        model.addAttribute("groups", document);
        return "groups";
    }

    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    public ModelAndView createGroup(ModelMap model, @RequestParam("name") String name){
        Group group = new Group();
        return saveGroup(group, name, model);
    }

    private ModelAndView saveGroup(Group group, String name, ModelMap model){
        group.setName(name);
        HttpRequestHelper.postHttp("createGroup", group, SERVER_URI);
        return new ModelAndView("redirect:/groups", model);
    }

    @RequestMapping(value = "/deleteGroup", method = RequestMethod.POST)
    public ModelAndView deleteGroup(ModelMap model, @RequestParam("id") String id){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpRequestHelper.httpRequest("deleteGroup?id=" + id, HttpMethod.DELETE, null, httpHeaders, SERVER_URI);
        return new ModelAndView("redirect:/groups", model);
    }

    @RequestMapping(value = "/groupsUpdate", method = RequestMethod.GET)
    public String updateGroup(Model model, @RequestParam("id") String id){
        Group group = groupService.findById(UUID.fromString(id));
        model.addAttribute("group", group);
        return "groupsUpdate";
    }

    @RequestMapping(value = "/groupsUpdate", method = RequestMethod.POST)
    public ModelAndView updateGroup(ModelMap model, @RequestParam("id") String id, @RequestParam("name") String name){
        Group group = groupService.findById(UUID.fromString(id));
        return saveGroup(group, name, model);
    }

    @RequestMapping(value = "/lessonsByGroup", method = RequestMethod.GET)
    public String watchLessonsByGroup(Model model, @RequestParam("id") String id){
        List<Lesson> lessons = lessonService.findByGroupId(UUID.fromString(id));
        List<Group> groups = groupService.findAll();
        List<Teacher> teachers = teacherService.findAll();
        model.addAttribute("lessons", lessons);
        model.addAttribute("groups", groups);
        model.addAttribute("teachers", teachers);
        model.addAttribute("byGroup", true);
        model.addAttribute("byTeacher", false);
        return "lessons";
    }

    @RequestMapping(value = "/lessons", method = RequestMethod.GET)
    public String watchLessons(Model model){
        addToModel(model, "lessons");
        return "lessons";
    }

    private void addToModel(Model model, String path){
        String element = HttpRequestHelper.getHttp(path, SERVER_URI);
        Document document = HttpRequestHelper.getDocument(element);
        Element temp = document.createElement("teachers");
        temp.setNodeValue(HttpRequestHelper.getHttp("teachers", SERVER_URI));
        model.addAttribute(path, document);
    }

    @RequestMapping(value = "/lessonsByTeacher", method = RequestMethod.GET)
    public String watchLessonsByTeacher(Model model, @RequestParam("id") String id){
        List<Lesson> lessons = lessonService.findByTeacherId(UUID.fromString(id));
        List<Group> groups = groupService.findAll();
        List<Teacher> teachers = teacherService.findAll();
        model.addAttribute("lessons", lessons);
        model.addAttribute("groups", groups);
        model.addAttribute("teachers", teachers);
        model.addAttribute("byGroup", false);
        model.addAttribute("byTeacher", true);
        return "lessons";
    }

    @RequestMapping(value = "/createLesson", method = RequestMethod.POST)
    public ModelAndView createLesson(ModelMap model, @RequestParam("name") String name, @RequestParam("startTime") String startTime,
                                     @RequestParam("group") String groupId, @RequestParam("teacher") String teacherId){
        if(checkIfBusy(groupId, startTime)){
            return new ModelAndView("redirect:/error?message=This date is busy by this group");
        }
        if(checkIfBusy(teacherId, startTime)){
            return new ModelAndView("redirect:/error?message=This date is busy by this Teacher");
        }

        Teacher teacher = teacherService.findById(UUID.fromString(teacherId));
        Group group = groupService.findById(UUID.fromString(groupId));

        Lesson lesson = new Lesson();
        return saveLesson(lesson, name, startTime, group, teacher, model);
    }

    private ModelAndView saveLesson(Lesson lesson, String name, String startTime, Group group, Teacher teacher, ModelMap model){
        lesson.setName(name);
        lesson.setStartTime(LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        lesson.setGroup(group);
        lesson.setTeacher(teacher);
        HttpRequestHelper.postHttp("createLesson", lesson, SERVER_URI);
        return new ModelAndView("redirect:/lessons", model);
    }
    private boolean checkIfBusy(String id, String startTime){
        if(lessonService.findByGroupId(UUID.fromString(id)).stream()
                .filter(lesson -> {
                    return LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).equals(lesson.getStartTime());
                }).count() > 0){
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/lessonsUpdate", method = RequestMethod.GET)
    public String updateLesson(Model model, @RequestParam("id") String id){
        Lesson lesson = lessonService.findById(UUID.fromString(id));
        List<Group> groups = groupService.findAll();
        List<Teacher> teachers = teacherService.findAll();
        model.addAttribute("lesson", lesson);
        model.addAttribute("groups", groups);
        model.addAttribute("teachers", teachers);
        return "lessonsUpdate";
    }

    @RequestMapping(value = "/lessonsUpdate", method = RequestMethod.POST)
    public ModelAndView updateLesson(ModelMap model, @RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("startTime") String startTime,
                                     @RequestParam("group") String groupId, @RequestParam("teacher") String teacherId){
        if(checkIfBusy(groupId, startTime)){
            return new ModelAndView("redirect:/error?message=This date is busy by this group");
        }
        if(checkIfBusy(teacherId, startTime)){
            return new ModelAndView("redirect:/error?message=This date is busy by this Teacher");
        }

        Group group = groupService.findById(UUID.fromString(groupId));
        Teacher teacher = teacherService.findById(UUID.fromString(teacherId));
        Lesson lesson = lessonService.findById(UUID.fromString(id));
        return saveLesson(lesson, name, startTime, group, teacher, model);
    }

    @RequestMapping(value = "/deleteLesson", method = RequestMethod.POST)
    public ModelAndView deleteLesson(ModelMap model, @RequestParam("id") String id){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpRequestHelper.httpRequest("deleteLesson?id=" + id, HttpMethod.DELETE, null, httpHeaders, SERVER_URI);
        return new ModelAndView("redirect:/lessons", model);
    }


    @RequestMapping(value = "/teachersByLesson", method = RequestMethod.GET)
    public String watchTeachersByLesson(Model model, @RequestParam("id") String id){
        Teacher teacher = teacherService.findById(UUID.fromString(id));
        model.addAttribute("teachers", Collections.singletonList(teacher));
        return "teachers";
    }

    @RequestMapping(value = "/teachers", method = RequestMethod.GET)
    public String watchTeachers(Model model){
        String element = HttpRequestHelper.getHttp("teachers", SERVER_URI);
        Document document = HttpRequestHelper.getDocument(element);
        model.addAttribute("teachers", document);
        return "teachers";
    }

    @RequestMapping(value = "/createTeacher", method = RequestMethod.POST)
    public ModelAndView createTeacher(ModelMap model, @RequestParam("firstName") String firstName,
                                      @RequestParam("lastName") String lastName, @RequestParam("position") String position){
        Teacher teacher = new Teacher();
        return saveTeacher(teacher, firstName, lastName, position, model);
    }

    private ModelAndView saveTeacher(Teacher teacher, String firstName, String lastName, String position, ModelMap model){
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setPosition(position);
        HttpRequestHelper.postHttp("createTeacher", teacher, SERVER_URI);
        return new ModelAndView("redirect:/teachers", model);
    }

    @RequestMapping(value = "/deleteTeacher", method = RequestMethod.POST)
    public ModelAndView deleteTeacher(ModelMap model, @RequestParam("id") String id){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpRequestHelper.httpRequest("deleteTeacher?id=" + id, HttpMethod.DELETE, null, httpHeaders, SERVER_URI);
        return new ModelAndView("redirect:/teachers", model);
    }

    @RequestMapping(value = "/teachersUpdate", method = RequestMethod.GET)
    public String updateTeacher(Model model, @RequestParam("id") String id){
        Teacher teacher = teacherService.findById(UUID.fromString(id));
        model.addAttribute("teacher", teacher);
        return "teachersUpdate";
    }

    @RequestMapping(value = "/teachersUpdate", method = RequestMethod.POST)
    public ModelAndView updateTeacher(ModelMap model, @RequestParam("id") String id, @RequestParam("firstName") String firstName,
                                      @RequestParam("lastName") String lastName, @RequestParam("position") String position){
        Teacher teacher = teacherService.findById(UUID.fromString(id));
        return saveTeacher(teacher, firstName, lastName, position, model);
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error(Model model, @RequestParam("message") String message){
        model.addAttribute("message", message);
        return "error";
    }

}
