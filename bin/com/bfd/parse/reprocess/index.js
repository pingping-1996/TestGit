define(["jquery", "web", "esd"], function(l, m) {
    return m.module.create({
        options: {
            hotelId: null
        },
        init: function() {
            this.initialData(),
                this.initializeDOM(),
                this.initializeEvent()
        },
        initialData: function() {
            this.travelTypeNames = ["其他类型", "家庭亲子", "情侣出行", "朋友出行", "商务出差", "独自旅行"],
                this.sourceTypeNames = ["", "来自手机APP", "来自手机网页", "来自短信", "", "", "来自携程"],
                this.rankNames = [{
                    id: 3,
                    name: "新手"
                }, {
                    id: 2,
                    name: "专家"
                }, {
                    id: 1,
                    name: "达人"
                }],
                this.commentReq = {
                    hotelId: this.hotelId,
                    recommendedType: 0,
                    pageIndex: 0,
                    roomTypeId: null,
                    mainTagId: 0,
                    subTagId: 0,
                    rankType: 0
                },
                this.commonCommentItemTemplate = '<li><div class="cmt_userinfo"><div class="cmt_pic"><span class="cmtpicico cmtpicico_bgc<#=colorIndex#>"><#=nickNameFirst#></span><p class="cmt_un"><#=nickName#></p><i class="uicon_level<#=rankData.id#>"><#=rankData.name#></i><p class="cmt_lvtxt"><#=travelType#></p></div></div><div class="cmt_info_mn"><div class="cmt_if_hd"><div class="if_hd"><span class="cmt_starbg"><span class="cmt_star" style="width:<#=(score / 5) * 84 + parseInt(score) * 4#>%"></span></span><b class="cmt_star_nmb"><#=score#></b><span class="cmt_tag"><#=roomType#></span></div><div class="if_hd_r"><span class="cmt_con_time"><#=createTime#></span><# if(source && source.length > 0){#><span class="cmt_con_phone"><#=source#></span><#}#></div></div><p class="cmt_txt"><#=content#><#=morrowHtml#></p><#=imageHtml#><#=replyHtml#></div></li>',
                this.commonCommentItemTemplate2 = '<li><div class="cmt_userinfo"><div class="cmt_pic"><span class="cmtpicico cmtpicico_bgc<#=colorIndex#>"><#=nickNameFirst#></span><p class="cmt_un"><#=nickName#></p><i class="uicon_level<#=rankData.id#>"><#=rankData.name#></i><p class="cmt_lvtxt"><#=travelType#></p></div></div><div class="cmt_info_mn"><div class="cmt_if_hd"><div class="if_hd"><span class="cmt_tag2"><#=roomType#></span></div><div class="if_hd_r"><span class="cmt_con_time"><#=createTime#></span><# if(source && source.length > 0){#><span class="cmt_con_phone"><#=source#></span><#}#></div></div><p class="cmt_txt"><#=content#><#=morrowHtml#></p><#=imageHtml#><#=replyHtml#></div></li>',
                this.commentReplyTemplate = '<div class="cmt_reply"><p><span method="replyContent" data-short="<#=replyShortContent#>" data-full="<#=replyContent#>"><# if(shortStatus){ #> <#=replyShortContent#> <#}else{#> <#=replyContent#> <#}#></span><# if(shortStatus){ #><span class="rp_more" method="replyToggleStatus" data-status="0" data-short="查看全部" data-full="收起">查看全部</span><#}#></p></div>',
                this.comment_no_Template = '<div class="hdetail_comt_no"><div class="hcomt_no_w"><i class="icon_no_comt left"></i><h4 class="t18 yahei">该条件下暂无点评~</h4><p>去看看其他条件下的点评吧~</p></div></div>'
        },
        initializeDOM: function() {
            this.reviewContainer = l("#review"),
                this.tagItems = this.reviewContainer.find("a[method='tagItem']"),
                this.hideTagItems = this.reviewContainer.find("a[method='tagItem'][data-hide='1']"),
                this.commentTypes = this.reviewContainer.find("li[method='commentType']"),
                this.roomTypeSelect = this.reviewContainer.find("select[method='commentRoomType']"),
                this.toggleTagStatusBtn = this.reviewContainer.find("a[method='toggleTagStatus']"),
                this.commentListContainer = this.reviewContainer.find("ul[method='commentList']")
        },
        initializeEvent: function() {
            this.reviewContainer.bind("click", m.addEvent(this, this.onReviewContainerClick)),
                this.reviewContainer.find("select").bind("change", m.addEvent(this, this.commentTypeChange))
        },
        onReviewContainerClick: function(t) {
            for (var e = m.event.element(t), a = e.attr("method"); !a; )
                a = (e = e.parent()).attr("method");
            if (!e.hasClass("on"))
                switch (a) {
                    case "toggleTagStatus":
                        1 != e.attr("data-datashowstatus") ? (this.hideTagItems.show(),
                            e.attr("data-datashowstatus", "1").html('<span class="s_c333">收起</span>')) : (this.hideTagItems.hide(),
                            e.attr("data-datashowstatus", "0").html('<span class="s_c333">展开</span>'));
                        break;
                    case "commentType":
                        this.commentTypes.removeClass("on"),
                            e.addClass("on"),
                            this.commentReq.recommendedType = e.attr("data-typeid"),
                            this.commentReq.pageIndex = 0,
                            this.getCommentListByPage();
                        break;
                    case "tagItem":
                        this.tagChange(e);
                        break;
                    case "replyToggleStatus":
                        var n = e.attr("data-status")
                            , i = e.parent().find("span[method='replyContent']");
                        "1" == n ? (e.attr("data-status", "0").text(e.attr("data-short")),
                            i.text(i.attr("data-short"))) : (e.attr("data-status", "1").text(e.attr("data-full")),
                            i.text(i.attr("data-full")));
                        break;
                    case "pageIndex":
                        this.commentReq.pageIndex = parseInt(e.attr("data-index")),
                            this.getCommentListByPage()
                }
        },
        commentTagChange: function(t) {
            if (t) {
                var e = this.reviewContainer.find("a[method='tagItem'][data-subid='" + t + "']");
                this.tagChange(e)
            }
        },
        tagChange: function(t) {
            this.tagItems.removeClass("on"),
                t.addClass("on"),
                this.commentReq.mainTagId = t.attr("data-mainid"),
                this.commentReq.subTagId = t.attr("data-subid"),
                this.commentReq.pageIndex = 0,
                this.getCommentListByPage()
        },
        commentTypeChange: function(t) {
            var e = m.event.element(t);
            switch (e.attr("method")) {
                case "commentRoomType":
                    this.commentReq.roomTypeId = e.val(),
                        this.commentReq.pageIndex = 0,
                        this.getCommentListByPage();
                    break;
                case "commentSort":
                    this.commentReq.rankType = e.val(),
                        this.commentReq.pageIndex = 0,
                        this.getCommentListByPage()
            }
        },
        loaded: !1,
        initCommentBox: function() {
            this.loaded || (this.loaded = !0,
                this.loadCommentTypes(),
                this.getCommentListByPage())
        },
        loadCommentTypes: function() {
            var n = this;
            m.ajax.exec(hotelPageController.basePath + pageCommonPath.getcommentroomtype, {
                hotelId: n.hotelId
            }, function(t) {
                if (t.value) {
                    html = '<option data-filterType="2" value="">不限房型</option>';
                    for (var e = 0; e < t.value.length; e++) {
                        var a = t.value[e];
                        html += '<option data-filterType="2" title="' + a.RoomtypeName + "\" value='" + a.RoomtypeId + "'>" + a.RoomtypeShortName + "(" + a.CommentCount + ")</option>"
                    }
                    n.roomTypeSelect.html(html).show()
                }
            })
        },
        getReqDataWithRisk: function() {
            var e = this;
            try {
                hotelPageController.needCtripRisk && (window.__cfpi = window.__cfpi || [],
                    window.__cfpi.push(["_getChloroToken", function(t) {
                        e.commentReq.cToken = t
                    }
                    ], !1)),
                hotelPageController.needElongRisk && (e.commentReq.eToken = window.fingerPrintToken || localStorage.getItem("_fid"))
            } catch (t) {}
            return delete this.commentReq.code,
                this.commentReq
        },
        getCommentListByPage: function(t) {
            var e = this;
            l("#commentLoading").show();
            var a = this.getReqDataWithRisk();
            window.location.href = "#review",
                m.ajax.exec(hotelPageController.basePath + pageCommonPath.getcheckcode, a, function(t) {
                    a.code = getCheckCodeByType(t.value, "comment"),
                        m.ajax.exec(hotelPageController.basePath + pageCommonPath.getcommentbypage, a, function(t) {
                            e.rendingCommonCommentList(t.value)
                        }, "GET", "JSON", 2e3, !1)
                }, "GET", "JSON", 5e3, !1)
        },
        rendingCommonCommentList: function(t) {
            if (l("#commentLoading").hide(),
            !t || t.Comments.length <= 0)
                return this.commentListContainer.html(this.comment_no_Template),
                    void l("#comment_paging").hide();
            for (var e = t.Comments, a = "", n = 0; n < e.length; n++) {
                var i = e[n]
                    , o = null != i.CommentUser ? i.CommentUser.NickName : "";
                8 < o.length && (o = o.substring(0, 8) + "...");
                var s = i.CommentScore ? i.CommentScore.Score : 0;
                a += m.template.convert(0 < s ? this.commonCommentItemTemplate : this.commonCommentItemTemplate2, {
                    colorIndex: (o.length + i.Content.length) % 8,
                    nickNameFirst: 0 < o.length ? o.substring(0, 1) : "",
                    travelType: this.travelTypeNameConvert(i.CommentExt.TravelType),
                    nickName: o,
                    rankData: this.userRankNameConvert(i.CommentUser.Rank),
                    createTime: i.CreateTime && 10 < i.CreateTime.length ? i.CreateTime.substr(0, 10) : i.CreateTime,
                    source: this.sourceTypeNameConvert(i.Source),
                    score: s.toFixed(1),
                    content: i.Content,
                    imageHtml: this.getImagesHTML(i.Images),
                    roomType: null != i.CommentExt && null != i.CommentExt.Order ? i.CommentExt.Order.RoomTypeName : "",
                    roomNum: null != i.CommentExt && null != i.CommentExt.Order && i.CommentExt.Order.RoomNum ? "房间号" + i.CommentExt.Order.RoomNum : "",
                    morrowHtml: i.IsMarrow ? '<i class="cmt_quality"></i>' : "",
                    replyHtml: this.getReplyHTML(i.Replys)
                })
            }
            0 < l.trim(a).length && this.commentListContainer.html(a),
                this.renderPageIndexHtml(t.TotalNumber),
                this.bingImgHover()
        },
        getReplyHTML: function(t) {
            var e = "";
            if (t && 0 < t.length)
                for (var a = 0; a < t.length; a++) {
                    var n = t[a].Content
                        , i = 80 < n.length
                        , o = i ? n.substr(0, 80) + "..." : "";
                    e += m.template.convert(this.commentReplyTemplate, {
                        replyContent: "酒店回复：" + n,
                        replyShortContent: "酒店回复：" + o,
                        shortStatus: i
                    })
                }
            return e
        },
        getImagesHTML: function(t) {
            var e = "";
            if (t && 0 < t.length) {
                e = '<p class="cmt_pic_lst">';
                for (var a = 0; a < t.length; a++) {
                    var n = t[a];
                    if (n && n.Path && 2 < n.Path.length && n.Path[1]) {
                        var i = n.Path[1].url;
                        e += '<span method="commentImg"><img src="' + i + '" data-bigImgUrl="' + (n.Path[2] ? n.Path[2].url : i) + '" height="60" width="60"></span>'
                    }
                }
                e += "</p>"
            }
            return e
        },
        renderPageIndexHtml: function(t) {
            var e = parseInt(t / 20) + (t % 20 != 0 ? 1 : 0);
            if (1 != e) {
                var a = this.commentReq.pageIndex
                    , n = 1
                    , i = 5
                    , o = !1
                    , s = !1;
                e < 7 ? i = e - 2 : a < i - 1 ? s = !0 : e - (i - 1) < a ? (o = !0,
                    n = e - (i + 1)) : (n = a - parseInt(i / 2),
                    s = o = !0);
                var m = "";
                0 < a ? (m += '<a href="javascript:void(0)" class="page_prev" method="pageIndex" data-index=\'' + (a - 1) + "'>上一页</a>",
                    m += '<a href="javascript:void(0)" method="pageIndex"  data-index="0">1</a>') : m += '<a href="javascript:void(0)" class="on">1</a>',
                o && (m += "<span>...</span>");
                for (var r = 0; r < i; r++) {
                    var c = n + r;
                    m += a == c ? '<a href="javascript:void(0)" class="on" >' + (c + 1) + "</a> " : '<a href="javascript:void(0)" method="pageIndex"  data-index=\'' + c + "' >" + (c + 1) + "</a> "
                }
                s && (m += "<span>...</span>"),
                    a != e - 1 ? (m += '<a href="javascript:void(0)" method="pageIndex"  data-index="' + (e - 1) + '" >' + e + "</a>",
                        m += '<a href="javascript:void(0)" class="page_next" method="pageIndex" data-index=\'' + (a + 1) + "'>下一页</a>") : m += '<a href="javascript:void(0)" class="on">' + e + "</a>",
                    l("#comment_paging").html(m).show()
            } else
                l("#comment_paging").html('<a href="javascript:void(0)" class="on">1</a>').show()
        },
        bingImgHover: function() {
            var e = l("#commentBigImgContainer")
                , a = e.find("img");
            this.reviewContainer.find("span[method='commentImg']").hover(function() {
                var t = l(this).find("img");
                a.attr("src", t.attr("data-bigImgUrl")),
                    e.css({
                        position: "absolute",
                        top: l(this).offset().top + 65,
                        left: l(this).offset().left,
                        "z-index": "10000"
                    }),
                    e.show()
            }, function() {
                e.css({
                    position: "relative"
                }),
                    a.attr("src", ""),
                    e.hide()
            })
        },
        travelTypeNameConvert: function(t) {
            var e = t > this.travelTypeNames.length - 1 || t < 0 ? 0 : t;
            return this.travelTypeNames[e]
        },
        sourceTypeNameConvert: function(t) {
            var e = t > this.sourceTypeNames.length - 1 || t < 0 ? 0 : t;
            return this.sourceTypeNames[e]
        },
        userRankNameConvert: function(t) {
            var e = t < 1 || 3 < t ? 0 : t - 1;
            return this.rankNames[e]
        }
    })
});
