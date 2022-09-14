package com.simonov.voting.controller.dish;

import com.simonov.voting.controller.MatcherFactory;
import com.simonov.voting.model.Dish;
import com.simonov.voting.to.DishTo;

import java.util.List;

import static com.simonov.voting.controller.AbstractControllerTest.NOW;

public class TestDataDish {

    public static final MatcherFactory.Matcher<Dish> DISH_MATCHER
            = MatcherFactory.usingIgnoringFieldsComparator(Dish.class, "restaurant");

    public static final MatcherFactory.Matcher<DishTo> DISH_TO_MATCHER
            = MatcherFactory.usingEqualsComparator(DishTo.class);

    public static final int DISH1_ID = 1;
    public static final int DISH13_ID = DISH1_ID + 12;

    public static final Dish cheb11 = new Dish(DISH1_ID, "беляш", 1, NOW.minusDays(1));
    public static final Dish cheb12 = new Dish(DISH1_ID + 1, "булочка", 2, NOW.minusDays(1));
    public static final Dish cheb13 = new Dish(DISH1_ID + 2, "кефир", 3, NOW.minusDays(1));
    public static final Dish cheb14 = new Dish(DISH1_ID + 3, "чебурек", 4, NOW.minusDays(1));
    public static final List<Dish> chebYesterday = List.of(cheb14, cheb13, cheb12, cheb11);
    public static final Dish rum11 = new Dish(DISH1_ID + 4, "самогон", 1, NOW.minusDays(1));
    public static final Dish rum12 = new Dish(DISH1_ID + 5, "виски", 2, NOW.minusDays(1));
    public static final Dish rum13 = new Dish(DISH1_ID + 6, "ром", 3, NOW.minusDays(1));
    public static final Dish rum14 = new Dish(DISH1_ID + 7, "коньяк", 4, NOW.minusDays(1));
    public static final List<Dish> rumYesterday = List.of(rum11, rum12, rum13, rum14);
    public static final Dish blin11 = new Dish(DISH1_ID + 8, "с творогом", 1, NOW.minusDays(1));
    public static final Dish blin12 = new Dish(DISH1_ID + 9, "с яблоком", 2, NOW.minusDays(1));
    public static final Dish blin13 = new Dish(DISH1_ID + 10, "с мясом", 3, NOW.minusDays(1));
    public static final Dish blin14 = new Dish(DISH1_ID + 11, "с овощами", 4, NOW.minusDays(1));
    public static final List<Dish> blinYesterday = List.of(blin11, blin12, blin13, blin14);
    public static final Dish cheb21 = new Dish(DISH1_ID + 12, "беляш2", 5, NOW);
    public static final Dish cheb22 = new Dish(DISH1_ID + 13, "булочка2", 6, NOW);
    public static final Dish cheb23 = new Dish(DISH1_ID + 14, "кефир2", 7, NOW);
    public static final Dish cheb24 = new Dish(DISH1_ID + 15, "чебурек2", 8, NOW);
    public static final List<Dish> chebTodayMenu = List.of(cheb24, cheb23, cheb22, cheb21);
    public static final Dish rum21 = new Dish(DISH1_ID + 16, "самогон2", 5, NOW);
    public static final Dish rum22 = new Dish(DISH1_ID + 17, "виски2", 6, NOW);
    public static final Dish rum23 = new Dish(DISH1_ID + 18, "ром2", 7, NOW);
    public static final Dish rum24 = new Dish(DISH1_ID + 19, "коньяк2", 8, NOW);
    public static final List<Dish> rumTodayMenu = List.of(rum24, rum23, rum22, rum21);
    public static final Dish blin21 = new Dish(DISH1_ID + 20, "с творогом2", 5, NOW);
    public static final Dish blin22 = new Dish(DISH1_ID + 21, "с яблоком2", 6, NOW);
    public static final Dish blin23 = new Dish(DISH1_ID + 22, "с мясом2", 7, NOW);
    public static final Dish blin24 = new Dish(DISH1_ID + 23, "с овощами2", 8, NOW);
    public static final List<Dish> blinTodayMenu = List.of(blin24, blin23, blin22, blin21);


}