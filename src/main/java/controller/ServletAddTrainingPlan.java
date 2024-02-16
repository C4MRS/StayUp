package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import model.trainingPlan.TrainingPlanDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@WebServlet("/addTrainingPlan")
public class ServletAddTrainingPlan extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        try {
            String emailPT= (String) session.getAttribute("email");
            String emailSub=request.getParameter("emailSub");
            List<Integer> allExercises=new ArrayList<>();
            String startDateStr = request.getParameter("dateStart");
            String endDateStr = request.getParameter("dateEnd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDateUtil=null;
            java.util.Date endDateUtil=null;
            try {
                startDateUtil = sdf.parse(startDateStr);
                endDateUtil = sdf.parse(endDateStr);
            } catch (ParseException e) {
                e.printStackTrace(); // e
            }
            java.sql.Date startDate = new java.sql.Date(startDateUtil.getTime());
            java.sql.Date endDate = new java.sql.Date(endDateUtil.getTime());

            LinkedHashMap<String, Object> trainingPlan = new LinkedHashMap<>();
            trainingPlan.put("Title", request.getParameter("title"));

            List<LinkedHashMap<String, Object>> days = new ArrayList<>();

            for (int dayNumber = 1; dayNumber <= 5; dayNumber++) {
                if (!request.getParameter("gg" + dayNumber).equals("0")) {
                    allExercises.add(Integer.parseInt(request.getParameter("gg" + dayNumber)));
                    LinkedHashMap<String, Object> day = new LinkedHashMap<>();
                    day.put("Day", String.valueOf(dayNumber));

                    List<LinkedHashMap<String, String>> exercisesArray = new ArrayList<>();
                    for (int i = 0; i < allExercises.get(dayNumber - 1); i++) {
                        LinkedHashMap<String, String> exercise = new LinkedHashMap<>();
                        exercise.put("Exercise", request.getParameter("formNameExerciseN" + (i+1) + "D" + dayNumber));
                        exercise.put("Sets", request.getParameter("formNameSetsN" + (i+1) + "D" + dayNumber));
                        exercise.put("Reps", request.getParameter("formNameRepsN" + (i+1) + "D" + dayNumber));
                        exercise.put("Rest", request.getParameter("formNamePauseN" + (i+1) + "D" + dayNumber));
                        exercisesArray.add(exercise);
                    }

                    day.put("Exercises", exercisesArray);

                    days.add(day);
                }
            }
            trainingPlan.put("Days", days);
            System.out.println(JSONObject.toJSONString(trainingPlan));
            Boolean state=new TrainingPlanDAO().addTrainingPlan(emailPT,emailSub,JSONObject.toJSONString(trainingPlan),startDate,endDate);

            if(!state)
                throw new Exception("Something went wrong!");

            request.setAttribute("success","./index.jsp");
            request.getRequestDispatcher("./infopages/success.jsp").forward(request,response);
        }catch (Exception e){
            request.setAttribute("exception",e);
            request.setAttribute("exceptionURL","./training_plan.jsp");
            request.getRequestDispatcher("./infopages/error.jsp").forward(request,response);
        }


    }
}