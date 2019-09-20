/* Slide show */

(function(){
	var screenshoot_slider_obj = {};

	screenshoot_slider_obj.callback = function() {
		screenshoot_slider_obj.addingClasses(this);
	};

	screenshoot_slider_obj.addingClasses = function(e) {

		$(e.$owlItems).removeClass('opacity80 opacity100 left-item center-item right-item');
		var $currentItems = $(e.$owlWrapper).find('.owl-item.active');
		var middleItemIndex;

		if ( e.options.items == 5 ) {
			middleItemIndex = $currentItems.eq(2).index();
		} else if ( e.options.items == 3 ) {
			middleItemIndex = $currentItems.eq(1).index();
		} else {
			middleItemIndex = $currentItems.eq(0).index();
		}

		$(e.$owlItems).each(function(){ 
			if ( $(this).index() < middleItemIndex ) {
				$(this).addClass('left-item');
			} else if ( $(this).index() > middleItemIndex ) {
				$(this).addClass('right-item');
			} else {
				$(this).addClass('center-item');
			}
		});
	};

	var $screenshoot_slider  = $('#screenshoot_slider');

	if ( $screenshoot_slider.length > 0 ) {
		$screenshoot_slider.owlCarousel({
			loop:true, 
		    stopOnHover : true,
			slideSpeed : 800,
			autoHeight : true,
			itemsCustom: [
				[320, 1],
				[450, 2],
				[580, 3],
				[992, 5]
			],
			addClassActive : true,
			afterInit : screenshoot_slider_obj.callback,
			afterMove : screenshoot_slider_obj.callback
		});

		screenshoot_slider_obj.slider = $screenshoot_slider.data('owlCarousel');

		$('.screenshoot-arrow').click(function(e){

			e.preventDefault();

			if ( $(this).hasClass('left-arrow') ) {
				screenshoot_slider_obj.slider.prev();
			} else {
				screenshoot_slider_obj.slider.next();
			}
		});
	}

}());


/* Scripts */
$(document).ready(function(){

	/* Parallax */
	$('div.bgParallax').each(function(){
		var $obj = $(this);	 
		$(".android-content").scroll(function() {
			var yPos = -($(window).scrollTop() / $obj.data('speed')); 
	 
			var bgpos = '50% '+ yPos + 'px';				 
			$obj.css('background-position', bgpos);
		}); 
	});

	/* Links */
	var position = window.scrollY || document.documentElement.scrollTop;
 	var top_about = $('#about').offset().top - 20;
	var top_app = $('#app').offset().top;

 	var $doc = $('.android-content');

	$('.navigation-effect-about').click(function() {		
		$doc.animate({
		    scrollTop: 0 + top_about 
		}, 1000);
	});

	$('.navigation-effect-app').click(function() {
		$doc.animate({
		    scrollTop: 0 + top_app 
		}, 1000);
	});

	/* Efects onload */
	$('.effect-load-top').each(function(){
		var $obje = $(this);	 

		$obje.on("load", function(){ 
			console.log('funcionaaaaaaaaaa');
		    alert("Image loaded.");
		});
	});
});
