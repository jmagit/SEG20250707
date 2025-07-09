using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TiendaAW.Data;
using TiendaAW.Entities;
using TiendaAW.Models;

namespace TiendaAW.Controllers {
    [Route("[controller]")]
    [ApiController]
    public class CategoriasController : ControllerBase {
        private readonly AdventureWorksContext _context;

        public CategoriasController(AdventureWorksContext context) {
            _context = context;
        }

        // GET: api/Categorias
        [HttpGet]
        [Produces("application/json", "application/xml")]
        public async Task<ActionResult<IEnumerable<Object>>> GetProductCategories() {
            return await _context.ProductCategories
                .Where(e => e.ParentProductCategoryId == null)
                .OrderBy(e => e.Name)
                .Select(e => new { id = e.ProductCategoryId, descripcion = e.Name })
                .ToListAsync();
        }

        [HttpGet("subcategorias")]
        [Produces("application/json", "application/xml")]
        public async Task<ActionResult<IEnumerable<Element<int, string>>>> GetSubCategories() {
            return await _context.ProductCategories
                .Where(e => e.ParentProductCategoryId != null)
                .OrderBy(e => e.Name)
                .Select(e => new Element<int, string>() { Key = e.ProductCategoryId, Value = e.Name })
                .ToListAsync();
        }

        // GET: api/Categorias/5
        [HttpGet("{id}")]
        [Produces("application/json", "application/xml")]
        [Authorize()]
        public async Task<ActionResult<ProductCategory>> GetProductCategory(int id) {
            var c = await _context.ProductCategories.FindAsync(id);

            if(c == null) {
                return NotFound();
            }

            return c;
        }
        [HttpGet("{id}/subcategorias")]
        [Produces("application/json", "application/xml")]
        public async Task<ActionResult<IEnumerable<Element<int, string>>>> GetProductSubCategory(int id) {
            var c = await _context.ProductCategories
                .Where(e => e.ProductCategoryId == id && e.ParentProductCategoryId == null)
                .Include(e => e.InverseParentProductCategory)
                .FirstOrDefaultAsync();

            if(c == null) {
                return NotFound();
            }

            return c.InverseParentProductCategory
                .OrderBy(e => e.Name)
                .Select(e => new Element<int, string>() { Key = e.ProductCategoryId, Value = e.Name })
                .ToList();
        }

        // PUT: api/Categorias/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutProductCategory(int id, ProductCategory productCategory) {
            if(id != productCategory.ProductCategoryId) {
                return BadRequest();
            }

            productCategory.ModifiedDate = DateTime.UtcNow;
            _context.Entry(productCategory).State = EntityState.Modified;

            try {
                await _context.SaveChangesAsync();
            } catch(DbUpdateConcurrencyException) {
                if(!ProductCategoryExists(id)) {
                    return NotFound();
                } else {
                    throw;
                }
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return NoContent();
        }

        // POST: api/Categorias
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<ProductCategory>> PostProductCategory(ProductCategory productCategory) {
            _context.ProductCategories.Add(productCategory);
            try {
                await _context.SaveChangesAsync();
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return CreatedAtAction("GetProductCategory", new { id = productCategory.ProductCategoryId }, productCategory);
        }

        // DELETE: api/Categorias/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteProductCategory(int id) {
            var productCategory = await _context.ProductCategories.FindAsync(id);
            if(productCategory == null) {
                return NotFound();
            }

            _context.ProductCategories.Remove(productCategory);
            try {
                await _context.SaveChangesAsync();
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return NoContent();
        }

        private bool ProductCategoryExists(int id) {
            return _context.ProductCategories.Any(e => e.ProductCategoryId == id);
        }
    }
}
