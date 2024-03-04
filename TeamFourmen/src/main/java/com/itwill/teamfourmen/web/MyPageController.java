package com.itwill.teamfourmen.web;

import com.itwill.teamfourmen.domain.Member;
import com.itwill.teamfourmen.domain.MemberRepository;
import com.itwill.teamfourmen.domain.NicknameInterceptor;
import com.itwill.teamfourmen.domain.Review;
import com.itwill.teamfourmen.domain.TmdbLike;
import com.itwill.teamfourmen.dto.MemberModifyDto;
import com.itwill.teamfourmen.dto.MemberSearchDto;
import com.itwill.teamfourmen.dto.movie.MovieDetailsDto;
import com.itwill.teamfourmen.dto.mypage.MypageDTO;
import com.itwill.teamfourmen.dto.person.DetailsPersonDto;
import com.itwill.teamfourmen.dto.review.CombineReviewDTO;
import com.itwill.teamfourmen.dto.tvshow.TvShowDTO;
import com.itwill.teamfourmen.service.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final FeatureService featureService;
    private final TvShowApiUtil tvShowApiUtil;
    private final MovieApiUtil movieApiUtil;
    private final MyPageService myPageService;
    private final PersonService personService;
    private final MemberService memberservice;
    private final NicknameInterceptor myname;
    
    @GetMapping("/")
    public void mypage() {
    }

    @GetMapping("/details/{id}/profile")
    public String getMyPageDetails(Model model, @PathVariable (name = "id") String email){
        log.info("get MY PAGE DETAILS USER EMAIL = {}", email);

        Member profile = myPageService.getMember(email);

        model.addAttribute("profile", profile);

        return "mypage/details-profile";
    }
    
    @GetMapping("/details/{id}/management")
    public String getManagementDetails(@RequestParam(name = "p", defaultValue = "0") int p,Model model, @PathVariable (name = "id") String email){
        log.info("get MY PAGE DETAILS USER EMAIL = {}", email);

		Page<Member> data = memberservice.getmemberlist(p);
	    model.addAttribute("data",data);
	    model.addAttribute("adminuser",email);

        return "mypage/admin";
    }
    
    @GetMapping("/details/{id}/edit")
    public String mypageedit() {
    	
    	return "mypage/edit";
    }

    
    @GetMapping("/details/{id}/admindetail/{email}")
    public String adminDetailPage(@PathVariable("id") String adminuser,
                                  @PathVariable("email") String email,
                                  Model model, HttpSession session) {
    	
        Member member = memberservice.getmemberdetail(email);
        model.addAttribute("members",member);  
        session.setAttribute("adminuser", adminuser);
        
        return "mypage/admindetail";
    	
    };
    
    @PostMapping("/myedit/update")
    public String updateUser(@ModelAttribute MemberModifyDto dto, HttpSession session) throws IllegalStateException, IOException {

        // 여기서 비밀번호를 비교하고 처리하면 됩니다.		 		
		String rootDirectory = File.listRoots()[0].getAbsolutePath();
		String sDirectory = rootDirectory + "ojng" + File.separator + "image";
		memberservice.update(dto, sDirectory);
		
		   // 세션에서 adminuser 가져오기
        String adminUserFromSession = myname.getMember().getEmail();

        // 리다이렉트할 URL을 생성
        String redirectUrl = "/mypage/details/" + adminUserFromSession + "/edit";
		
        return "redirect:" + redirectUrl;

    }
    
    @PostMapping("/detail/update")
    public String updateadmin(@ModelAttribute MemberModifyDto dto, HttpSession session) throws IllegalStateException, IOException {
        // 여기서 비밀번호를 비교하고 처리하면 됩니다.
        String sDirectory = "C:/image";
        memberservice.update(dto, sDirectory);
        
        // 세션에서 adminuser 가져오기
        String adminUserFromSession = (String) session.getAttribute("adminuser");

        // 리다이렉트할 URL을 생성
        String redirectUrl = "/mypage/details/" + adminUserFromSession + "/management";

        // 리다이렉트
        return "redirect:" + redirectUrl;
    }
    
    @GetMapping("/admindelete")
    public String admindelete(@RequestParam(name = "email") String email, HttpSession session) {
       
        
        memberservice.deleteByEmail(email);
        
        // 세션에서 adminuser 가져오기
        String adminUserFromSession = (String) session.getAttribute("adminuser");

        // 리다이렉트할 URL을 생성
        String redirectUrl = "/mypage/details/" + adminUserFromSession + "/management";

        // 리다이렉트
        return "redirect:" + redirectUrl;
    }
    

    @GetMapping("/delete")
    public String delete(@RequestParam(name = "email") String email) {
       
        
        memberservice.deleteByEmail(email);
        
        return "redirect:/logout";
    }
    
   
 
	  @GetMapping("/details/{id}/search")
	    public String search(@ModelAttribute MemberSearchDto dto, @PathVariable("id") String adminuser, Model model) {
	        log.info("search(dto={})", dto);
	        
	        // Service 메서드 호출 -> 검색 결과 -> Model -> View
	        Page<Member> data = memberservice.search(dto);
	        model.addAttribute("data", data);
	        model.addAttribute("adminuser",adminuser);
	        log.info("data={}",data);
	        
	        return "mypage/search";
	    };
    
    
    @GetMapping("/details/{id}/reviews")
    public String getReviews(Model model, @PathVariable( name = "id") String email){


        List<Review> myAllReview =  featureService.getAllMyReview(email);

        model.addAttribute("myAllReview", myAllReview);

        List<CombineReviewDTO> combineInfoList = new ArrayList<>();

        Map<Long, Integer> reviewComment = new HashMap<>();
        Map<Long, Long> reviewLiked = new HashMap<>();

        for(Review myReview : myAllReview) {
            int tmdb_id = myReview.getTmdbId();

            Long reviewId = myReview.getReviewId();

            int numOfComment = featureService.getNumOfReviewComment(reviewId);
            Long numOfLiked = featureService.getNumOfReviewLike(reviewId);

            reviewLiked.put(reviewId, numOfLiked);
            reviewComment.put(reviewId, numOfComment);

            String category = myReview.getCategory();

            CombineReviewDTO combineReviewDTO = new CombineReviewDTO();
            switch (category) {
                case "tv":
                    TvShowDTO tvShowDTO = tvShowApiUtil.getTvShowDetails(tmdb_id);

                    combineReviewDTO.setId(tvShowDTO.getId());
                    combineReviewDTO.setName(tvShowDTO.getName());
                    combineReviewDTO.setCategory(category);
                    combineReviewDTO.setPosterPath(tvShowDTO.getPoster_path());

                    combineInfoList.add(combineReviewDTO);

                    continue;
                case "movie":
                    MovieDetailsDto movieDTO = movieApiUtil.getMovieDetails(tmdb_id);

                    combineReviewDTO.setId(movieDTO.getId());
                    combineReviewDTO.setName(movieDTO.getTitle());
                    combineReviewDTO.setCategory(category);
                    combineReviewDTO.setPosterPath(movieDTO.getPosterPath());

                    combineInfoList.add(combineReviewDTO);

                    continue;
                default:
                    log.info("NOPE");
            }
        }

        model.addAttribute("numOfReviewLiked", reviewLiked);
        model.addAttribute("numOfReviewComment", reviewComment);

        log.info("COMBINE LIST = {} ",combineInfoList);

        model.addAttribute("combineInfoList" , combineInfoList);

        return "mypage/details-review-list";
    }

    @GetMapping("/details/{id}/{category}")
    public String getLikedList(Model model, @PathVariable(name = "id") String email, @PathVariable(name = "category") String category){
        log.info("GET LIKED LIST - EMAIL = {}, CATEGORY = {}", email, category);

        Member member = Member.builder().email(email).build();

        List<TmdbLike> likedList = featureService.getLikedList(member, category);
        List<MypageDTO> myPageLikedList = new ArrayList<>();

        for (TmdbLike works : likedList) {
            MypageDTO mypageDTO = new MypageDTO(); // 각 반복에서 새로운 객체 생성

            switch (category) {
                case "movie":
                    MovieDetailsDto movieDto = movieApiUtil.getMovieDetails(works.getTmdbId());
                    mypageDTO.setId(movieDto.getId());
                    mypageDTO.setName(movieDto.getTitle());
                    mypageDTO.setCategory(category);
                    mypageDTO.setImagePath(movieDto.getPosterPath());

                    myPageLikedList.add(mypageDTO);

                    continue;

                case "tv":
                    TvShowDTO tvShowDTO = tvShowApiUtil.getTvShowDetails(works.getTmdbId());
                    mypageDTO.setId(tvShowDTO.getId());
                    mypageDTO.setName(tvShowDTO.getName());
                    mypageDTO.setCategory(category);
                    mypageDTO.setImagePath(tvShowDTO.getPoster_path());

                    myPageLikedList.add(mypageDTO);

                    continue;

                case "person":
                    DetailsPersonDto personDto = personService.getPersonDetailsEnUS(works.getTmdbId());
                    mypageDTO.setId(personDto.getId());
                    mypageDTO.setName(personDto.getName());
                    mypageDTO.setCategory(category);
                    mypageDTO.setImagePath(personDto.getProfilePath());

                    myPageLikedList.add(mypageDTO);

                default:
                    log.info("없어요!!!");
                    break;
            }

        }

        log.info("LIKED LIST = {}", myPageLikedList);
        model.addAttribute("likedList", myPageLikedList);

        return "mypage/details-liked-list";
    }

}
