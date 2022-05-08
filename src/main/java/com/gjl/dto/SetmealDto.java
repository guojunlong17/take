package com.gjl.dto;

import com.gjl.domain.Setmeal;
import com.gjl.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
