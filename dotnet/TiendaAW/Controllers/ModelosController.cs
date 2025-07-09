using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TiendaAW.Data;
using TiendaAW.Entities;
using TiendaAW.Models;

namespace TiendaAW.Controllers {
    [Route("[controller]")]
    [ApiController]
    public class ModelosController : ControllerBase {
        private readonly AdventureWorksContext _context;

        public ModelosController(AdventureWorksContext context) {
            _context = context;
        }

        // GET: api/Modelos
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Element<int, string>>>> GetProductModels() {
            return await _context.ProductModels
                .OrderBy(e => e.Name)
                .Select(e => new Element<int, string>() { Key = e.ProductModelId, Value = e.Name })
                .ToListAsync();
        }

        // GET: api/Modelos/5
        [HttpGet("{id}")]
        public async Task<ActionResult<ProductModel>> GetProductModel(int id) {
            var productModel = await _context.ProductModels.FindAsync(id);

            if(productModel == null) {
                return NotFound();
            }

            return productModel;
        }

        // PUT: api/Modelos/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutProductModel(int id, ProductModel productModel) {
            if(id != productModel.ProductModelId) {
                return BadRequest();
            }
            productModel.ModifiedDate = DateTime.UtcNow;
            _context.Entry(productModel).State = EntityState.Modified;

            try {
                await _context.SaveChangesAsync();
            } catch(DbUpdateConcurrencyException) {
                if(!ProductModelExists(id)) {
                    return NotFound();
                } else {
                    throw;
                }
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return NoContent();
        }

        // POST: api/Modelos
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<ProductModel>> PostProductModel(ProductModel productModel) {
            _context.ProductModels.Add(productModel);
            await _context.SaveChangesAsync();

            return CreatedAtAction("GetProductModel", new { id = productModel.ProductModelId }, productModel);
        }

        // DELETE: api/Modelos/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteProductModel(int id) {
            var productModel = await _context.ProductModels.FindAsync(id);
            if(productModel == null) {
                return NotFound();
            }

            _context.ProductModels.Remove(productModel);
            try {
                await _context.SaveChangesAsync();
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return NoContent();
        }

        private bool ProductModelExists(int id) {
            return _context.ProductModels.Any(e => e.ProductModelId == id);
        }
    }
}
