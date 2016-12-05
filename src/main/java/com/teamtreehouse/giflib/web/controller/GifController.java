package com.teamtreehouse.giflib.web.controller;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.teamtreehouse.giflib.model.Gif;
import com.teamtreehouse.giflib.service.CategoryService;
import com.teamtreehouse.giflib.service.GifService;
import com.teamtreehouse.giflib.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GifController {
    @Autowired
    private GifService gifService;

    @Autowired
    private CategoryService categoryService;

    // Home page - index of all GIFs
    @RequestMapping("/")
    public String listGifs(Model model) {
        List<Gif> gifs = gifService.findAll();
        model.addAttribute("gifs", gifs);
        return "gif/index";
    }

    // Single GIF page
    @RequestMapping("/gifs/{gifId}")
    public String gifDetails(@PathVariable Long gifId, Model model) {
        Gif gif = gifService.findById(gifId);
        model.addAttribute("gif", gif);
        return "gif/details";
    }

    // GIF image data
    @RequestMapping("/gifs/{gifId}.gif")
    @ResponseBody
    public byte[] gifImage(@PathVariable Long gifId) {
        return gifService.findById(gifId).getBytes();
    }

    // Favorites - index of all GIFs marked favorite
    @RequestMapping("/favorites")
    public String favorites(Model model) {
        List<Gif> faves = new ArrayList<>();
        model.addAttribute("gifs",faves);
        model.addAttribute("username","Chris Ramacciotti"); // Static username
        return "gif/favorites";
    }

    // Upload a new GIF
    @RequestMapping(value = "/gifs", method = RequestMethod.POST)
    public String addGif(Gif gif, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        gifService.save(gif, file);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Gif successfully uploaded!", FlashMessage.Status.SUCCESS));
        return String.format("redirect:/gifs/%s", gif.getId());
    }

    // Form for uploading a new GIF
    @RequestMapping("/upload")
    public String formNewGif(Model model) {
        if(!model.containsAttribute("gif")) {
            model.addAttribute("gif", new Gif());
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("action", "/gifs");
        model.addAttribute("heading", "Upload");
        model.addAttribute("submit", "Add");
        return "gif/form";
    }

    // Form for editing an existing GIF
    @RequestMapping(value = "/gifs/{gifId}/edit")
    public String formEditGif(@PathVariable Long gifId, Model model) {
        if(!model.containsAttribute("gif")) {
            model.addAttribute("gif", gifService.findById(gifId));
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("action", String.format("/gifs/%s",gifId));
        model.addAttribute("heading", "Edit GIF");
        model.addAttribute("submit", "Update");
        return "gif/form";
    }

    // Update an existing GIF
    @RequestMapping(value = "/gifs/{gifId}", method = RequestMethod.POST)
    public String updateGif(@Valid Gif gif, @RequestParam MultipartFile file, BindingResult result, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category", result);
            redirectAttributes.addFlashAttribute(gif);
            return String.format("redirect:/gifs/%s", gif.getId());
        }
        gifService.save(gif, file);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("GIF successfully updated!", FlashMessage.Status.SUCCESS));
        return String.format("redirect:/gifs/%s", gif.getId());
    }

    // Delete an existing GIF
    @RequestMapping(value = "/gifs/{gifId}/delete", method = RequestMethod.POST)
    public String deleteGif(@PathVariable Long gifId, RedirectAttributes redirectAttributes) {
        Gif gif = gifService.findById(gifId);
        gifService.delete(gif);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("GIF successfully deleted!", FlashMessage.Status.SUCCESS));
       return "redirect:/";
    }

    // Mark/unmark an existing GIF as a favorite
    @RequestMapping(value = "/gifs/{gifId}/favorite", method = RequestMethod.POST)
    public String toggleFavorite(@PathVariable Long gifId, HttpServletRequest request) {
        Gif gif = gifService.findById(gifId);
        gifService.toggleFavorite(gif);

        // TODO: This is NOT SAFE
        // TODO: Find a new way to redirect to referer
        return String.format("redirect:%s", request.getHeader("referer"));
    }

    // Search results
    @RequestMapping("/search")
    public String searchResults(@RequestParam String q, Model model) {
        // TODO: Get list of GIFs whose description contains value specified by q
        List<Gif> gifs = new ArrayList<>();

        model.addAttribute("gifs",gifs);
        return "gif/index";
    }
}