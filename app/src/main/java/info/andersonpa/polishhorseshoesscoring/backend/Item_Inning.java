package info.andersonpa.polishhorseshoesscoring.backend;

import info.andersonpa.polishhorseshoesscoring.db.Throw;

public class Item_Inning {
    public int inning;
    public Throw pl_throw;
    public Throw pr_throw;

    public Item_Inning(int inning) {
        this.inning = inning;
    }

    public Item_Inning(int inning, Throw t1) {
        this(inning);
        pl_throw = t1;
    }

    public Item_Inning(int inning, Throw t1, Throw t2) {
        this(inning, t1);
        pr_throw = t2;
    }
    
    public Integer getInning() {
        return inning;
    }
    
    public Throw getPL_throw() {
        return pl_throw;
    }

    public void setPL_throw(Throw p_throw) {
        this.pl_throw = p_throw;
    }
    
    public Throw getPR_throw() {
        return pr_throw;
    }
    
    public void setPR_throw(Throw p_throw) {
        this.pr_throw = p_throw;
    }
}
