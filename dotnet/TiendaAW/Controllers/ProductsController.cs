using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TiendaAW.Data;
using TiendaAW.Entities;
using TiendaAW.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;

namespace TiendaAW.Controllers {
    [Route("[controller]")]
    [ApiController]
    public class ProductsController : ControllerBase {
        private readonly AdventureWorksContext _context;

        public ProductsController(AdventureWorksContext context) {
            _context = context;
        }

        // GET: api/Products
        [HttpGet]
        [Produces("application/json", "application/xml")]
        [EndpointSummary("Recupera la lista de productos")]
        public async Task<ActionResult<Object>> GetProducts([Description("número de página en base 0")] [FromQuery] int? page, [FromQuery] int size = 20) {
            if(page.HasValue) {
                var result = new PageModel<ProductShort>();
                result.Number = page.Value;
                result.TotalElements = _context.Products.Count();
                result.TotalPages = (int)Math.Floor((double)result.TotalElements / size);
                result.Content = await _context.Products
                    .OrderBy(e => e.Name)
                    .Skip(page.Value * size).Take(size)
                    .Select(e => ProductShort.from(e))
                    .ToListAsync();
                result.Size = result.Content.Count;
                return result;
            }
            return await _context.Products.Select(e => ProductShort.from(e)).ToListAsync();
        }

        [HttpGet("libre")]
        public async Task<ActionResult<Object>> GetEjemplos() {

            //return await _context.Products
            //    .Select(e => new { id = e.ProductId, producto = e.Name })
            //    .ToListAsync();
            //return await _context.Products
            //    .FromSql($"SELECT ProductId, Name FROM [SalesLT].[Product]")
            //    .Select(e => new { id = e.ProductId, producto = e.Name })
            //    .ToListAsync();
            return await _context.Database
                .SqlQuery<ProductShort>($"SELECT ProductId, Name FROM [SalesLT].[Product]")
                .Select(e => new { id = e.ProductId, producto = e.Name })
                .ToListAsync();
        }

        // GET: api/Products/5
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(ProductEdit))]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<Object>> GetProduct(int id, [FromQuery][Description("edit o detail")] string mode = "edit") {
            IQueryable<Object> query = _context.Products
                .Where(item => item.ProductId == id);
            if(mode == "edit")
                query = query.Select(item => ProductEdit.from(item as Product));
            else if(mode == "detail")
                query = query.Include(p => (p as Product).ProductCategory)
                    .Include(p => (p as Product).ProductModel)
                    .Select(item => ProductDetail.from(item as Product));
            else
                return BadRequest(mode);

            var product = await query.FirstOrDefaultAsync();
            if(product == null) {
                return NotFound();
            }
            return product;
        }

        [HttpGet("{id}/photo")]
        public async Task<ActionResult<Object>> GetThumbNailPhoto(int id) {
            var product = await _context.Products.FindAsync(id);

            if(product == null) {
                return NotFound();
            }
            return File(product.ThumbNailPhoto, "image/gif");
        }

        // POST: api/Products
        [HttpPost]
        public async Task<ActionResult<Product>> PostProduct(ProductEdit product) {
            var item = product.merge(new Product());
            _context.Products.Add(item);
            try {
                await _context.SaveChangesAsync();
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return CreatedAtAction("GetProduct", new { id = item.ProductId });
        }

        // PUT: api/Products/5
        [HttpPut("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public async Task<IActionResult> PutProduct(int id, ProductEdit product) {
            if(id != product.ProductId) {
                return BadRequest();
            }
            var item = await _context.Products.FindAsync(id);

            if(item == null) {
                return NotFound();
            }
            product.merge(item);
            //_context.Entry(ProductEdit.from(product)).State = EntityState.Modified;

            try {
                await _context.SaveChangesAsync();
            } catch(DbUpdateConcurrencyException) {
                if(!ProductExists(id)) {
                    return NotFound();
                } else {
                    throw;
                }
            }

            return NoContent();
        }

        // DELETE: api/Products/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteProduct(int id) {
            var product = await _context.Products.FindAsync(id);
            if(product == null) {
                return NotFound();
            }

            _context.Products.Remove(product);
            try {
                await _context.SaveChangesAsync();
            } catch(Exception ex) {
                return Problem(ex.InnerException?.Message ?? ex.Message, statusCode: 400);
            }

            return NoContent();
        }

        private bool ProductExists(int id) {
            return _context.Products.Any(e => e.ProductId == id);
        }
    }
}
