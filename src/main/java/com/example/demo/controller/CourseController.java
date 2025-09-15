package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 1. ✅ Kết nối với index.html (Home page)
    @GetMapping("/")
    public String viewHomePage(Model model) {
        return findPaginated(1, "courseName", "asc", model);
    }

    // 2. ✅ Kết nối với new_course.html (Add form)
    @GetMapping("/add")
    public String showNewCourseForm(Model model) {
        Course course = new Course(); // Tạo empty Course object
        model.addAttribute("course", course); // ← Quan trọng cho th:object="${course}"
        return "new_course"; // → trỏ tới new_course.html
    }

    // 3. ✅ Xử lý form submission (từ cả add và update)
    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("course") Course course) {
        courseService.saveCourse(course); // Save/Update course
        return "redirect:/"; // → redirect về trang chính
    }

    // 4. ✅ Kết nối với update_course.html (Update form)
    @GetMapping("/update/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model) {
        Course course = courseService.getCourseById(id); // Lấy course từ DB
        model.addAttribute("course", course); // ← Quan trọng cho th:object="${course}"
        return "update_course"; // → trỏ tới update_course.html
    }

    // 5. ✅ Xử lý delete operation
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable(value = "id") long id) {
        courseService.deleteCourseById(id); // Xóa course
        return "redirect:/"; // → redirect về trang chính
    }

    // 6. ✅ Xử lý pagination cho index.html
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 5;

        Page<Course> page = courseService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Course> listCourses = page.getContent();

        // ✅ Thêm các attributes cần thiết cho index.html
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listCourses", listCourses); // ← Data cho table

        return "index"; // → trỏ tới index.html
    }
}
