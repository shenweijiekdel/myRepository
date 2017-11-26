package cn.tedu.examsystem.controller;

import cn.tedu.examsystem.pojo.Answer;
import cn.tedu.examsystem.pojo.Exam;
import cn.tedu.examsystem.pojo.Option;
import cn.tedu.examsystem.pojo.Question;
import cn.tedu.examsystem.service.ExamService;
import cn.tedu.examsystem.service.QuestionService;
import javafx.scene.input.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/exam/")
public class ExamController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ExamService examService;
    @InitBinder
    public void initBinder(WebDataBinder binder) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    @RequestMapping("addQuestionBlank.html")
    public String addQuestionBlank(int examId,Question question,String[] questionOption,int[] questionAnswer,Model model){
        System.out.println("examId=" + examId);
        List<Option> options = new ArrayList<Option>();
        List<Answer> answers = new ArrayList<Answer>();
        question.setpId(UUID.randomUUID().toString());
        question.seteId(examId);


        for (String opt:questionOption){
        Option option = new Option();
            String oId = UUID.randomUUID().toString();
         option.setoContent(opt);
         option.setpId(question.getpId());
         option.setoId(oId);
         options.add(option);
        }
        for (int asw:questionAnswer){
            Answer answer = new Answer();
            answer.setoId(options.get(asw-1).getoId()); /*刚好options中的索引为asw-1个，遍历数组把答案找出来*/
            answer.setpId(question.getpId());
            answers.add(answer);
        }
        question.setAnswers(answers);
        question.setOptions(options);
        questionService.putQuestionIntoBlank(question);
        return "redirect:/exam/showExamInfo.html?examid=" + question.geteId();
    }
    @RequestMapping("createExam.html")
    public String createExam(Exam exam,Model model){

        examService.createExam(exam);

        return "redirect:/exam/displayExam.html";
    }
    @RequestMapping("displayExam.html")
    public String displayExam(Model model){
        List<Exam> exams = examService.displayExams();
        System.out.println(exams.size());
        if (exams != null)
        model.addAttribute("exams",exams);
        return "home";
    }
    @RequestMapping("showExamInfo.html")
    public String showExamInfo(Model model,int examid){
        List<Question> list = null;
        model.addAttribute("examId",examid);
        list =  questionService.findAll(examid);
        if (list != null)
            model.addAttribute("questions",list);
        return "showExamInfo";
    }
    @RequestMapping("deleteExam.html")
    public String deleteExam(int examid,Model model){
        examService.deleteExam(examid);
        return "redirect:/exam/displayExam.html";
    }


}