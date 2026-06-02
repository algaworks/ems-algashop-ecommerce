(function($) {
	"use strict"

	// Mobile Nav toggle
	$('.menu-toggle > a').on('click', function (e) {
		e.preventDefault();
		$('#responsive-nav').toggleClass('active');
	})

	// Fix cart dropdown from closing
	$('.cart-dropdown').on('click', function (e) {
		e.stopPropagation();
	});

	/////////////////////////////////////////

	// Products Slick
	$('.products-slick').each(function() {
		var $this = $(this),
				$nav = $this.attr('data-nav');

		$this.slick({
			slidesToShow: 4,
			slidesToScroll: 1,
			autoplay: true,
			infinite: true,
			speed: 300,
			dots: false,
			arrows: true,
			appendArrows: $nav ? $nav : false,
			responsive: [{
	        breakpoint: 991,
	        settings: {
	          slidesToShow: 2,
	          slidesToScroll: 1,
	        }
	      },
	      {
	        breakpoint: 480,
	        settings: {
	          slidesToShow: 1,
	          slidesToScroll: 1,
	        }
	      },
	    ]
		});
	});

	// Products Widget Slick
	$('.products-widget-slick').each(function() {
		var $this = $(this),
				$nav = $this.attr('data-nav');

		$this.slick({
			infinite: true,
			autoplay: true,
			speed: 300,
			dots: false,
			arrows: true,
			appendArrows: $nav ? $nav : false,
		});
	});

	/////////////////////////////////////////

	// Product Main img Slick
	$('#product-main-img').slick({
    infinite: true,
    speed: 300,
    dots: false,
    arrows: true,
    fade: true,
    asNavFor: '#product-imgs',
  });

	// Product imgs Slick
  $('#product-imgs').slick({
    slidesToShow: 3,
    slidesToScroll: 1,
    arrows: true,
    centerMode: true,
    focusOnSelect: true,
		centerPadding: 0,
		vertical: true,
    asNavFor: '#product-main-img',
		responsive: [{
        breakpoint: 991,
        settings: {
					vertical: false,
					arrows: false,
					dots: true,
        }
      },
    ]
  });

	// Product img zoom
	var zoomMainProduct = document.getElementById('product-main-img');
	if (zoomMainProduct) {
		$('#product-main-img .product-preview').zoom();
	}

	/////////////////////////////////////////

	// Input number
	$('.input-number').each(function() {
		var $this = $(this),
		$input = $this.find('input[type="number"]'),
		up = $this.find('.qty-up'),
		down = $this.find('.qty-down');

		down.on('click', function () {
			var value = parseInt($input.val()) - 1;
			value = value < 1 ? 1 : value;
			$input.val(value);
			$input.change();
			updatePriceSlider($this , value)
		})

		up.on('click', function () {
			var value = parseInt($input.val()) + 1;
			$input.val(value);
			$input.change();
			updatePriceSlider($this , value)
		})
	});

	var priceInputMax = document.getElementById('price-max');
    var priceInputMin = document.getElementById('price-min');

    if (priceInputMax != null ){
        priceInputMax.addEventListener('change', function(){
            updatePriceSlider($(this).parent() , this.value)
        });
    }

	if (priceInputMin != null ){
        priceInputMin.addEventListener('change', function(){
            updatePriceSlider($(this).parent() , this.value)
        });
	}

	function updatePriceSlider(elem , value) {
		if ( elem.hasClass('price-min') ) {
			priceSlider.noUiSlider.set([value, null]);
		} else if ( elem.hasClass('price-max')) {
			priceSlider.noUiSlider.set([null, value]);
		}
	}

	// Price Slider
	var priceSlider = document.getElementById('price-slider');
	if (priceSlider) {
		noUiSlider.create(priceSlider, {
			start: [priceInputMin.value,
			    priceInputMax.value ? priceInputMax.value : 9999
			],
			connect: true,
			step: 1,
			range: {
				'min': 1,
				'max': 9999
			}
		});

		priceSlider.noUiSlider.on('update', function( values, handle ) {
			var value = values[handle];
			handle ? priceInputMax.value = value : priceInputMin.value = value
		});
	}

	$('.js-product-on-click').on('click', function(){
        let href = $(this).find('.js-product-on-click-link').first().attr("href");
        if (href) {
            window.location = href;
        }
    });

    $('.js-on-click-remove-item').on('click', function(){
      let itemId = $(this).data('item-id');
      let token = $("meta[name='_csrf']").attr("content");
      let fieldName = $("meta[name='_csrf_parameter_name']").attr("content");
      let request = {
        url: "/shopping-cart/remove-item/" + itemId,
        method: "POST",
        data: {
            "_csrf": token
        }
      };
      $.ajax(request)
      .done(function() {
        window.location = "/shopping-cart?removed=true";
      })
      .fail(function() {
        alert( "error" );
      });
    });

//    $('.js-on-click-remove-item').on('click', function(){
//         let href = $(this).find('.js-product-on-click-link').first().attr("href");
//         if (href) {
//             window.location = href;
//         }
//    });

    function generateToken() {
        let request = {
            number: $('#cardForm').find('input[name="cardNumber"]').val(),
            holderName: $('#cardForm').find('input[name="holderName"]').val(),
            holderDocument: $('#cardForm').find('input[name="holderName"]').val(),
            expirationDate: $('#cardForm').find('input[name="cardExpiration"]').val(),
            cvv: $('#cardForm').find('input[name="CVV"]').val()
        }

        let token = $('#publicCardToken').val();
        let url = $('#cardForm').attr('action');
        let response = {};

        $.ajax({
            contentType : "application/json",
            async: false, //Força esperar o request terminar para lançar o submit do form
            data: JSON.stringify(
                request
            ),
            headers: {
                'Authorization': token
            },
            dataType: "json",
            converters: {
                'text json': true
            },
            url:  url,
            type: "POST",
            beforeSend: function(xhr) {
            },
            error: function (data) {
                response.fail = true;
                response.userMessage = 'Cartão inválido, verifique as informações e tente novamente.';
             },
            success: function(data) {
                let tokenizedCard = JSON.parse(data).tokenizedCard;
                response.fail = false;
                response.tokenizedCard = tokenizedCard;
            }
        });

        return response;
    }

    $('#checkoutForm').submit(function(e) {
        //e.preventDefault();
        let paymentMethod = $('input[name="paymentMethod"]:checked').val();

        if (!paymentMethod) {
            return false;
        }

        if (paymentMethod != 'CREDIT_CARD') {
            return true;
        }

        let cardResponse = generateToken();
        console.log(cardResponse);

        if (cardResponse.fail == true) {
             return false;
        }

        $('#cardTokenInput').val(cardResponse.tokenizedCard);

        return true;
    });

})(jQuery);
