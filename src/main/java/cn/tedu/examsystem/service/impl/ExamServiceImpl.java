package cn.tedu.examsystem.service.impl;

import cn.tedu.examsystem.mapper.AnswerMapper;
import cn.tedu.examsystem.mapper.ExamMapper;
import cn.tedu.examsystem.mapper.OptionMapper;
import cn.tedu.examsystem.mapper.QuestionMapper;
import cn.tedu.examsystem.pojo.*;
import cn.tedu.examsystem.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private OptionMapper optionMapper;
    @Autowired
    private AnswerMapper answerMapper;
    public void createExam(Exam exam){
        examMapper.saveExam(exam);
    }

    public List<Exam> displayExams() {
        return examMapper.findAllExam();
    }

    public void paperJudge(String[] answers, int examId, int stuId) {
        List<Answer> sAnswers = new ArrayList<Answer>();

        String[] que = new String[answers.length];
        for (int i = 0; i <answers.length; i++) {
            Answer sAnswer = new Answer();
            sAnswer.setpId(answers[i].split(",")[0]);
            sAnswer.setoId(answers[i].split(",")[1]);
            sAnswers.add(sAnswer);
        }




        List<Question> questions = questionMapper.findById(examId,sAnswers);
        float eveScore = 100 / questions.size();
        float sScore = 0;
        boolean flag  = false;
        for (Question question:questions
                ) {
            List<Answer> qAnswers = question.getAnswers();
            List<Answer> sAnswer = new ArrayList<Answer>();
            for (Answer an:sAnswers){
                if (an.getpId().equals(question.getpId()))
                    sAnswer.add(an);
            }
            Collections.sort(sAnswer);
            Collections.sort(qAnswers);
            if (qAnswers.equals(sAnswer)){
                System.out.println("对");
                sScore += eveScore;
            }
            else
                System.out.println("错");
        }
        scoreRegist(stuId,Float.parseFloat(new  DecimalFormat("#.0").format(sScore)));

    }

    @Transactional
    public void deleteExam(int examid) {
        Exam exam = examMapper.findExambyId(examid);
        List<Answer> answers;
        List<Option> options;
        List<Question> questions = exam.getQuestions();
        for (Question question:questions){
            if ((answers = question.getAnswers()).size() > 0)
            answerMapper.deleteAnswerByOid(answers);
            if ((options = question.getOptions()).size() > 0)
            optionMapper.deleteOptionByPid( options);

        }
    if (questions.size() > 0)
        questionMapper.deleteQuestionByEid(questions);
        examMapper.deleteExamById(exam.geteId());
    }

    public void scoreRegist(int stuId, float score) {
        examMapper.scoreRegist(stuId,score);
    }
}
